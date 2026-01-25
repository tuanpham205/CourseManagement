package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Integer> {
    @Query("SELECT COUNT(r) FROM RegistrationRequest r")
    long countTotalLeads();
    @Query(value = "SELECT SUM(c.price) FROM registration_requests r " +
            "JOIN courses c ON r.desired_course_name = c.course_name " +
            "WHERE r.status = 'NEW'", nativeQuery = true)
    BigDecimal getPendingRevenue();
}
