package com.vesco.financetracker.controller;

import com.vesco.financetracker.dto.AnalyticsSummary;
import com.vesco.financetracker.dto.CategoryReportEntry;
import com.vesco.financetracker.dto.MonthlyReportEntry;
import com.vesco.financetracker.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummary> getSummary(
            @AuthenticationPrincipal com.vesco.financetracker.security.AppUserPrincipal principal) {
        if (principal == null)
            return ResponseEntity.status(401).build();
        AnalyticsSummary summary = analyticsService.getSummary(principal.getId());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyReportEntry>> getMonthly(
            @AuthenticationPrincipal com.vesco.financetracker.security.AppUserPrincipal principal) {
        if (principal == null)
            return ResponseEntity.status(401).build();
        List<MonthlyReportEntry> report = analyticsService.getMonthlyReport(principal.getId(), 6);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryReportEntry>> getCategories(
            @AuthenticationPrincipal com.vesco.financetracker.security.AppUserPrincipal principal) {
        if (principal == null)
            return ResponseEntity.status(401).build();
        List<CategoryReportEntry> report = analyticsService.getCategoryReport(principal.getId());
        return ResponseEntity.ok(report);
    }
}
