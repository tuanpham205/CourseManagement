package com.team5.quanlyhocvu.service;

import com.team5.quanlyhocvu.repository.InvoiceRepository;
import com.team5.quanlyhocvu.repository.RegistrationRequestRepository;
import com.team5.quanlyhocvu.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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

    // ================== BÁO CÁO TỔNG HỢP ==================
    public Map<String, Object> getReportSummary(LocalDate fromDate, LocalDate toDate) {

        Map<String, Object> report = new HashMap<>();

        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to   = toDate.atTime(23, 59, 59);

        // 1. Doanh thu
        BigDecimal doanhThuThucTe =
                invoiceRepo.sumRevenueBetween(from, to);

        BigDecimal doanhThuDuKien =
                requestRepo.sumPendingRevenueBetween(from, to);

        // 2. Lead
        long tongLead =
                requestRepo.countLeadsBetween(from, to);

        // 3. Học viên
        long tongHocVien =
                studentRepo.countStudentsBetween(fromDate, toDate);

        // ================== TRẢ KẾT QUẢ TIẾNG VIỆT ==================
        report.put("Từ ngày", fromDate);
        report.put("Đến ngày", toDate);

        report.put("Tổng số lead", tongLead);
        report.put("Tổng số học viên", tongHocVien);

        report.put("Doanh thu thực tế", doanhThuThucTe != null ? doanhThuThucTe : BigDecimal.ZERO);
        report.put("Doanh thu dự kiến", doanhThuDuKien != null ? doanhThuDuKien : BigDecimal.ZERO);

        return report;
    }
    public List<Map<String, Object>> getRevenueByTop3Courses(
            LocalDate fromDate,
            LocalDate toDate
    ) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to   = toDate.atTime(23, 59, 59);

        List<Object[]> raw = invoiceRepo.sumRevenueByCourse(from, to);

        BigDecimal total = raw.stream()
                .limit(3)
                .map(r -> (BigDecimal) r[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return raw.stream()
                .limit(3)
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    String courseName = (String) r[0];
                    BigDecimal revenue = (BigDecimal) r[1];

                    BigDecimal percent = revenue
                            .multiply(BigDecimal.valueOf(100))
                            .divide(total, 2, BigDecimal.ROUND_HALF_UP);

                    map.put("tenKhoaHoc", courseName);
                    map.put("doanhThu", revenue);
                    map.put("tyLeDongGop", percent);

                    return map;
                })
                .toList();
    }

}
