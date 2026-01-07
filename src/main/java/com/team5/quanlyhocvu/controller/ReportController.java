package com.team5.quanlyhocvu.controller;

import com.team5.quanlyhocvu.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/finance")
    public ResponseEntity<Map<String, Object>> getFinance() {
        return ResponseEntity.ok(reportService.getFinanceSummary());
    }

    @GetMapping("/leads-count")
    public ResponseEntity<Long> getLeads() {
        return ResponseEntity.ok(reportService.getLeadsCount());
    }

    @GetMapping("/students-count")
    public ResponseEntity<Long> getStudents() {
        return ResponseEntity.ok(reportService.getStudentsCount());
    }
}
