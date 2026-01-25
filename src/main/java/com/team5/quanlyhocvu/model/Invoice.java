package com.team5.quanlyhocvu.model;

import com.team5.quanlyhocvu.model.enums.InvoiceStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_request_id", nullable = false)
    private Integer registrationRequestId;
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;
    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Invoice() {}

    public Invoice(
            Integer registrationRequestId,
            Course course,
            BigDecimal totalAmount,
            LocalDate dueDate
    ) {
        this.registrationRequestId = registrationRequestId;
        this.course = course;
        this.totalAmount = totalAmount;
        this.paidAmount = BigDecimal.ZERO;
        this.dueDate = dueDate;
        this.status = InvoiceStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getRegistrationRequestId() { return registrationRequestId; }
    public void setRegistrationRequestId(Integer registrationRequestId) { this.registrationRequestId = registrationRequestId; }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }


    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", registrationRequestId=" + registrationRequestId +
                ", courseId=" + course.getId() +
                ", totalAmount=" + totalAmount +
                ", paidAmount=" + paidAmount +
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}