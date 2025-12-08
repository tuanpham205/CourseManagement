package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.Invoice;
import com.team5.quanlyhocvu.model.enums.InvoiceStatus;
import com.team5.quanlyhocvu.repository.InvoiceRepository;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }
    // 1. TẠO HÓA ĐƠN BAN ĐẦU
    @Transactional
    public Invoice createInvoice(Integer registrationRequestId, Integer courseId, BigDecimal totalAmount, LocalDate dueDate) {
        // Kiểm tra xem Lead này đã có hóa đơn chưa để tránh trùng lặp
        if (invoiceRepository.findByRegistrationRequestId(registrationRequestId).isPresent()) {
            throw new IllegalStateException("Lead ID " + registrationRequestId + " đã có hóa đơn.");
        }

        Invoice invoice = new Invoice(registrationRequestId, courseId, totalAmount, dueDate);
        // mặc định là PENDING
        return invoiceRepository.save(invoice);
    }
    // 2. XỬ LÝ GIAO DỊCH (Được gọi bởi PaymentService)
    @Transactional
    public Invoice processPayment(Long invoiceId, BigDecimal paymentAmount) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn ID: " + invoiceId));

        // Kiểm tra trạng thái không hợp lệ
        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Hóa đơn đang ở trạng thái không cho phép nhận tiền (Đã PAID/Đã Hủy).");
        }

        // Tính toán số tiền còn lại (để kiểm tra)(tránh trả dư)
        BigDecimal remainingAmount = invoice.getTotalAmount().subtract(invoice.getPaidAmount());

        // LOGIC NGHIỆP VỤ CỐT LÕI: CHỈ CHO PHÉP THANH TOÁN TOÀN BỘ (Pay All)
        if (paymentAmount.compareTo(remainingAmount) < 0) {
            throw new IllegalArgumentException("Hóa đơn phải được thanh toán toàn bộ. Số tiền còn thiếu: " + remainingAmount.subtract(paymentAmount));
        }

        // Cập nhật số tiền và trạng thái
        invoice.setPaidAmount(invoice.getPaidAmount().add(paymentAmount));
        invoice.setStatus(InvoiceStatus.PAID);

        return invoiceRepository.save(invoice);
    }


    // 3. KIỂM TRA NGHIỆP VỤ (Dùng trong StudentService)
    /**
     * Kiểm tra xem hóa đơn của Lead đã được thanh toán đầy đủ hay chưa.
     * @param registrationRequestId ID của Lead (Yêu cầu tư vấn).
     * @return true nếu trạng thái là PAID.
     */
    public boolean checkPaymentStatus(Integer registrationRequestId) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findByRegistrationRequestId(registrationRequestId);

        if (optionalInvoice.isEmpty()) {
            // Nếu không tìm thấy hóa đơn cho Lead(yêu cầu tư vấn) này, coi như chưa thanh toán
            return false;
        }

        return optionalInvoice.get().getStatus() == InvoiceStatus.PAID;
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
}