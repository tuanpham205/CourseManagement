package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface RegistrationRequestRepository
        extends JpaRepository<RegistrationRequest, Integer> {

    // Số lượng lead trong khoảng thời gian
    @Query("""
        SELECT COUNT(r)
        FROM RegistrationRequest r
        WHERE r.createdAt BETWEEN :from AND :to
    """)
    long countLeadsBetween(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to
    );


    // Doanh thu dự kiến từ lead NEW trong khoảng thời gian
    @Query(value = """
        SELECT COALESCE(SUM(c.price), 0)
        FROM registration_requests r
        JOIN courses c ON r.desired_course_name = c.course_name
        WHERE r.status = 'NEW'
          AND r.created_at BETWEEN :from AND :to
    """, nativeQuery = true)
    BigDecimal sumPendingRevenueBetween(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to
    );
}
