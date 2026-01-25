package com.team5.quanlyhocvu.controller;

import com.team5.quanlyhocvu.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Báo cáo tổng hợp:
     * - Tổng lead
     * - Tổng học viên
     * - Doanh thu thực tế
     * - Doanh thu dự kiến
     */
    // vi du GET /api/admin/reports/summary?from=2024-01-01&to=2024-01-31

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getReportSummary(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return ResponseEntity.ok(
                reportService.getReportSummary(from, to)
        );
    }
    @GetMapping("/course-revenue")
    public ResponseEntity<List<Map<String, Object>>> getCourseRevenue(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return ResponseEntity.ok(
                reportService.getRevenueByTop3Courses(from, to)
        );
    }

}
