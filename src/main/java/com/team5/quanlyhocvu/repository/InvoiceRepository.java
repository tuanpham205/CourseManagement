package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Tìm hóa đơn bằng ID của Lead (RegistrationRequest ID)
     */
    Optional<Invoice> findByRegistrationRequestId(Integer registrationRequestId);
    @Query("SELECT SUM(i.paidAmount) FROM Invoice i")
    BigDecimal sumTotalRevenue();

    @Query("SELECT SUM(i.totalAmount - i.paidAmount) FROM Invoice i")
    BigDecimal sumTotalDebt();
}