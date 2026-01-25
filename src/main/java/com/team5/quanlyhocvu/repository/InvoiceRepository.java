package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.Invoice;
import com.team5.quanlyhocvu.model.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Tìm hóa đơn theo RegistrationRequest ID
     */
    Optional<Invoice> findByRegistrationRequestId(Integer registrationRequestId);

    /**
     * Tổng doanh thu (chỉ invoice đã PAID)
     */
    @Query("""
        SELECT COALESCE(SUM(i.paidAmount), 0)
        FROM Invoice i
        WHERE i.status = 'PAID'
    """)
    BigDecimal sumTotalRevenue();

    /**
     * Tổng công nợ (chưa thu đủ tiền)
     */
    @Query("""
        SELECT COALESCE(SUM(i.totalAmount - i.paidAmount), 0)
        FROM Invoice i
        WHERE i.status <> 'PAID'
    """)
    BigDecimal sumTotalDebt();

    /**
     * Doanh thu trong khoảng thời gian
     */
    @Query("""
        SELECT COALESCE(SUM(i.paidAmount), 0)
        FROM Invoice i
        WHERE i.status = 'PAID'
          AND i.createdAt BETWEEN :from AND :to
    """)
    BigDecimal sumRevenueBetween(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to
    );

    /**
     * Doanh thu theo khóa học (IELTS / TOEIC / VSTEP)
     */
    @Query("""
    SELECT c.courseName, SUM(i.paidAmount)
    FROM Invoice i
    JOIN i.course c
    WHERE i.status = 'PAID'
      AND i.createdAt BETWEEN :from AND :to
    GROUP BY c.courseName
    ORDER BY SUM(i.paidAmount) DESC
""")
    List<Object[]> sumRevenueByCourse(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to
    );


}
