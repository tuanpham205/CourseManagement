package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoice_Id(Long invoiceId);

    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}