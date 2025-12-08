package com.team5.quanlyhocvu.model;

import com.team5.quanlyhocvu.model.enums.PaymentMethod;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // THAY THẾ studentId BẰNG MỐI QUAN HỆ VỚI INVOICE
    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT")
    private String note; // Đổi tên description thành note để rõ hơn

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now(); // Tự động set

    // SỬ DỤNG ENUM CHO PHƯƠNG THỨC THANH TOÁN
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod method;

    // Trường status đã bị XÓA vì trạng thái thuộc về Invoice

    // --- Constructor ---

    public Payment() {
    }

    // Constructor cho Service tạo mới
    public Payment(Invoice invoice, BigDecimal amount, String note, PaymentMethod method) {
        this.invoice = invoice;
        this.amount = amount;
        this.note = note;
        this.method = method;
        this.paymentDate = LocalDateTime.now();
    }

    // --- Getter và Setter ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; } // Thay Description bằng Note

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    // (Bổ sung hàm toString() nếu cần)
}