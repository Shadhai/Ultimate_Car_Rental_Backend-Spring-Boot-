package com.carrental.controller.driver;

import com.carrental.dto.response.ApiResponse;
import com.carrental.dto.response.DriverEarningsDto;
import com.carrental.dto.response.DriverPayoutResponseDto;
import com.carrental.enums.PayoutStatus;
import com.carrental.service.driver.DriverPayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/driver/payouts")
@RequiredArgsConstructor
public class DriverPayoutController {
    
    private final DriverPayoutService payoutService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverPayoutResponseDto>>> getDriverPayouts(
            @RequestHeader("X-User-Id") Long driverId) {
        List<DriverPayoutResponseDto> response = payoutService.getDriverPayouts(driverId);
        return ResponseEntity.ok(ApiResponse.success("Payouts retrieved successfully", response));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<DriverPayoutResponseDto>>> getPayoutsByStatus(
            @RequestHeader("X-User-Id") Long driverId,
            @PathVariable PayoutStatus status) {
        List<DriverPayoutResponseDto> response = payoutService.getPayoutsByStatus(driverId, status);
        return ResponseEntity.ok(ApiResponse.success("Payouts retrieved", response));
    }
    
    @GetMapping("/{payoutId}")
    public ResponseEntity<ApiResponse<DriverPayoutResponseDto>> getPayoutDetails(
            @RequestHeader("X-User-Id") Long driverId,
            @PathVariable Long payoutId) {
        DriverPayoutResponseDto response = payoutService.getPayoutDetails(payoutId, driverId);
        return ResponseEntity.ok(ApiResponse.success("Payout details retrieved", response));
    }
    
    @GetMapping("/earnings")
    public ResponseEntity<ApiResponse<DriverEarningsDto>> getEarningsSummary(
            @RequestHeader("X-User-Id") Long driverId) {
        DriverEarningsDto response = payoutService.getEarningsSummary(driverId);
        return ResponseEntity.ok(ApiResponse.success("Earnings summary retrieved", response));
    }
    
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<DriverPayoutResponseDto>>> getPayoutHistory(
            @RequestHeader("X-User-Id") Long driverId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<DriverPayoutResponseDto> response = payoutService.getPayoutHistory(driverId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Payout history retrieved", response));
    }
    
    @GetMapping("/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalEarnings(
            @RequestHeader("X-User-Id") Long driverId) {
        BigDecimal response = payoutService.getTotalEarnings(driverId);
        return ResponseEntity.ok(ApiResponse.success("Total earnings calculated", response));
    }
    
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getAvailableBalance(
            @RequestHeader("X-User-Id") Long driverId) {
        BigDecimal response = payoutService.getAvailableBalance(driverId);
        return ResponseEntity.ok(ApiResponse.success("Available balance retrieved", response));
    }
}