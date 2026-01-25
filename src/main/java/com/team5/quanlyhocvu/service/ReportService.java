package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.repository.InvoiceRepository;
import com.team5.quanlyhocvu.repository.RegistrationRequestRepository;
import com.team5.quanlyhocvu.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

        // Doanh thu thực tế (Tổng từ Invoice)
        BigDecimal actualRevenue = invoiceRepo.sumTotalRevenue();

        // Doanh thu dự kiến (Tự động tính bằng cách khớp tên khóa học với bảng Course)
        BigDecimal pendingRevenue = requestRepo.getPendingRevenue();

        map.put("actualRevenue", actualRevenue != null ? actualRevenue : BigDecimal.ZERO);
        map.put("pendingRevenue", pendingRevenue != null ? pendingRevenue : BigDecimal.ZERO);

        return map;
    }


    public long getLeadsCount() {
        return requestRepo.countTotalLeads();
    }

    public long getStudentsCount() {
        return studentRepo.countTotalStudents();
    }
}