package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.model.Invoice;
import com.team5.quanlyhocvu.model.Payment;
import com.team5.quanlyhocvu.model.enums.PaymentMethod;
import com.team5.quanlyhocvu.repository.PaymentRepository;
import com.team5.quanlyhocvu.service.exception.ResourceNotFoundException; // Cần import ResourceNotFoundException
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceService invoiceService;

    public PaymentService(PaymentRepository paymentRepository, InvoiceService invoiceService) {
        this.paymentRepository = paymentRepository;
        this.invoiceService = invoiceService;
    }


    // 1. GHI NHẬN GIAO DỊCH
    @Transactional
    public Payment recordPayment(Long invoiceId, BigDecimal amount, String methodStr, String note) {

        // 1. Kiểm tra và cập nhật Invoice
        // Nếu processPayment ném ra exception (vì thiếu tiền hoặc Invoice không hợp lệ),
        // giao dịch sẽ KHÔNG được ghi nhận (Transactional rollback).
        Invoice updatedInvoice = invoiceService.processPayment(invoiceId, amount);

        // 2. Tạo Entity Payment để lưu trữ lịch sử giao dịch
        PaymentMethod method;
        try {
            // Chuyển chuỗi thành Enum PaymentMethod
            method = PaymentMethod.valueOf(methodStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ: " + methodStr);
        }

        // Tạo Payment Entity, sử dụng updatedInvoice và PaymentMethod
        Payment payment = new Payment(updatedInvoice, amount, note, method);

        // 3. Lưu Payment (Ghi nhận Phiếu thu)
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch ID: " + id));
    }

    public List<Payment> getPaymentsByInvoiceId(Long invoiceId) {
        return paymentRepository.findByInvoice_Id(invoiceId);
    }

}