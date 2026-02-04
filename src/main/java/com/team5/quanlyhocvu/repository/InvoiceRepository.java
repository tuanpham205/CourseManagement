package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.Invoice;
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
     * 1. DOANH THU THỰC TẾ (Hóa đơn đã thanh toán - PAID)
     * Sử dụng paidAmount để chính xác số tiền đã thu
     */
    @Query("""
        SELECT COALESCE(SUM(i.paidAmount), 0)
        FROM Invoice i
        WHERE i.status = 'PAID'
          AND i.createdAt BETWEEN :from AND :to
    """)
    BigDecimal sumRevenueBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * 2. DOANH THU DỰ KIẾN (Hóa đơn chưa thanh toán - UNPAID)
     * Lấy tổng tiền (totalAmount) của các hóa đơn chưa thu được tiền
     */
    @Query("""
        SELECT COALESCE(SUM(i.totalAmount), 0)
        FROM Invoice i
        WHERE i.status = 'UNPAID'
          AND i.createdAt BETWEEN :from AND :to
    """)
    BigDecimal sumPendingRevenue(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * 3. DOANH THU THEO KHÓA HỌC (Top 3)
     * Lưu ý: Vì bảng Course không có giá, ta lấy tên khóa học từ Join
     * và lấy số tiền từ bảng Invoice.
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
    List<Object[]> sumRevenueByCourse(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}