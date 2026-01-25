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
        // Lấy dữ liệu 1 lần để tối ưu hiệu năng
        BigDecimal revenue = invoiceRepo.sumTotalRevenue();
        BigDecimal debt = invoiceRepo.sumTotalDebt();

        // Trả về 0 nếu null để Frontend không bị lỗi hiển thị
        map.put("totalRevenue", revenue != null ? revenue : 0);
        map.put("totalDebt", debt != null ? debt : 0);

        return map;
    }


    public long getLeadsCount() {
        return requestRepo.countTotalLeads();
    }

    public long getStudentsCount() {
        return studentRepo.countTotalStudents();
    }
}