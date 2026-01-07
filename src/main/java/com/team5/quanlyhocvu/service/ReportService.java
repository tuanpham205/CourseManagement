package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.repository.InvoiceRepository;
import com.team5.quanlyhocvu.repository.RegistrationRequestRepository;
import com.team5.quanlyhocvu.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {
    private final InvoiceRepository invoiceRepo;
    private final RegistrationRequestRepository requestRepo;
    private final StudentRepository studentRepo;

    public ReportService(InvoiceRepository invoiceRepo,
                         RegistrationRequestRepository requestRepo,
                         StudentRepository studentRepo) {
        this.invoiceRepo = invoiceRepo;
        this.requestRepo = requestRepo;
        this.studentRepo = studentRepo;
    }

    public Map<String, Object> getFinanceSummary() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalRevenue",
                invoiceRepo.sumTotalRevenue() == null ? 0 : invoiceRepo.sumTotalRevenue()
        );
        map.put("totalDebt",
                invoiceRepo.sumTotalDebt() == null ? 0 : invoiceRepo.sumTotalDebt()
        );
        return map;
    }


    public long getLeadsCount() {
        return requestRepo.countTotalLeads();
    }

    public long getStudentsCount() {
        return studentRepo.countTotalStudents();
    }
}