package com.team5.quanlyhocvu.repository;

import com.team5.quanlyhocvu.model.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Integer> {
    @Query("SELECT COUNT(r) FROM RegistrationRequest r")
    long countTotalLeads();
}
