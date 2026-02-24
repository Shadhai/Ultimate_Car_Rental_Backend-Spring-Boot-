package com.carrental.controller.admin;

import com.carrental.dto.response.AdminDashboardStatsDto;
import com.carrental.dto.response.ApiResponse;
import com.carrental.dto.response.RevenueReportDto;
import com.carrental.service.admin.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {
    
    private final AdminReportService reportService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardStatsDto>> getDashboardStats() {
        AdminDashboardStatsDto response = reportService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", response));
    }
    
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<RevenueReportDto>> getRevenueReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        RevenueReportDto response = reportService.getRevenueReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Revenue report generated", response));
    }
    
    // Remove or fix the monthly report if you don't need it
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {
        // If you want to keep this, you need to implement it
        // For now, return empty or remove this endpoint
        Map<String, Object> response = new HashMap<>();
        return ResponseEntity.ok(ApiResponse.success("Monthly report not implemented", response));
    }
}