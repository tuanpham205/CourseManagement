package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.Course;
import com.team5.quanlyhocvu.model.Invoice;
import com.team5.quanlyhocvu.model.enums.InvoiceStatus;
import com.team5.quanlyhocvu.repository.CourseRepository;
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
    private final CourseRepository courseRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          CourseRepository courseRepository) {
        this.invoiceRepository = invoiceRepository;
        this.courseRepository = courseRepository;
    }

    // 1. TẠO HÓA ĐƠN BAN ĐẦU
    @Transactional
    public Invoice createInvoice(
            Integer registrationRequestId,
            Integer courseId,
            BigDecimal totalAmount,
            LocalDate dueDate
    ) {
        // Tránh tạo trùng hóa đơn cho cùng 1 lead
        if (invoiceRepository.findByRegistrationRequestId(registrationRequestId).isPresent()) {
            throw new IllegalStateException(
                    "Lead ID " + registrationRequestId + " đã có hóa đơn."
            );
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy khóa học ID: " + courseId)
                );

        Invoice invoice = new Invoice(
                registrationRequestId,
                course,
                totalAmount,
                dueDate
        );

        return invoiceRepository.save(invoice);
    }

    // 2. XỬ LÝ THANH TOÁN
    @Transactional
    public Invoice processPayment(Long invoiceId, BigDecimal paymentAmount) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy hóa đơn ID: " + invoiceId)
                );

        if (invoice.getStatus() == InvoiceStatus.PAID
                || invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Hóa đơn đang ở trạng thái không cho phép nhận tiền."
            );
        }

        BigDecimal remainingAmount =
                invoice.getTotalAmount().subtract(invoice.getPaidAmount());

        // CHỈ CHO THANH TOÁN TOÀN BỘ
        if (paymentAmount.compareTo(remainingAmount) < 0) {
            throw new IllegalArgumentException(
                    "Hóa đơn phải được thanh toán toàn bộ. Còn thiếu: "
                            + remainingAmount.subtract(paymentAmount)
            );
        }

        invoice.setPaidAmount(invoice.getPaidAmount().add(paymentAmount));
        invoice.setStatus(InvoiceStatus.PAID);

        return invoiceRepository.save(invoice);
    }

    // 3. KIỂM TRA ĐÃ THANH TOÁN CHƯA (StudentService dùng)
    public boolean checkPaymentStatus(Integer registrationRequestId) {
        return invoiceRepository.findByRegistrationRequestId(registrationRequestId)
                .map(invoice -> invoice.getStatus() == InvoiceStatus.PAID)
                .orElse(false);
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
}
