package com.carrental.controller.admin;

import com.carrental.dto.response.ApiResponse;
import com.carrental.dto.response.DriverPayoutResponseDto;
import com.carrental.enums.PayoutStatus;
import com.carrental.service.admin.AdminPayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/payouts")
@RequiredArgsConstructor
public class AdminPayoutController {
    
    private final AdminPayoutService payoutService;
    
    @PostMapping("/rental/{rentalId}")
    public ResponseEntity<ApiResponse<DriverPayoutResponseDto>> generatePayoutForRental(@PathVariable Long rentalId) {
        DriverPayoutResponseDto response = payoutService.generatePayoutForRental(rentalId);
        return ResponseEntity.ok(ApiResponse.success("Payout generated successfully", response));
    }
    
    @PostMapping("/{payoutId}/process")
    public ResponseEntity<ApiResponse<DriverPayoutResponseDto>> processPayout(
            @PathVariable Long payoutId,
            @RequestParam String paymentMethod,
            @RequestParam String transactionReference) {
        DriverPayoutResponseDto response = payoutService.processPayout(payoutId, paymentMethod, transactionReference);
        return ResponseEntity.ok(ApiResponse.success("Payout processed successfully", response));
    }
    
    @PostMapping("/{payoutId}/fail")
    public ResponseEntity<ApiResponse<DriverPayoutResponseDto>> markPayoutAsFailed(
            @PathVariable Long payoutId,
            @RequestParam String reason) {
        DriverPayoutResponseDto response = payoutService.markAsFailed(payoutId, reason);
        return ResponseEntity.ok(ApiResponse.success("Payout marked as failed", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverPayoutResponseDto>>> getAllPayouts() {
        List<DriverPayoutResponseDto> response = payoutService.getAllPayouts();
        return ResponseEntity.ok(ApiResponse.success("Payouts retrieved successfully", response));
    }
    
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<DriverPayoutResponseDto>>> getPayoutsByPage(Pageable pageable) {
        Page<DriverPayoutResponseDto> response = payoutService.getPayoutsByPage(pageable);
        return ResponseEntity.ok(ApiResponse.success("Payouts page retrieved", response));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<DriverPayoutResponseDto>>> getPayoutsByStatus(@PathVariable PayoutStatus status) {
        List<DriverPayoutResponseDto> response = payoutService.getPayoutsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Payouts by status retrieved", response));
    }
    
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<DriverPayoutResponseDto>>> getPayoutsByDriver(@PathVariable Long driverId) {
        List<DriverPayoutResponseDto> response = payoutService.getPayoutsByDriver(driverId);
        return ResponseEntity.ok(ApiResponse.success("Driver payouts retrieved", response));
    }
    
    @GetMapping("/pending/amount")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalPendingAmount() {
        BigDecimal response = payoutService.getTotalPendingAmount();
        return ResponseEntity.ok(ApiResponse.success("Total pending amount calculated", response));
    }
    
    @GetMapping("/paid/amount")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalPaidAmount() {
        BigDecimal response = payoutService.getTotalPaidAmount();
        return ResponseEntity.ok(ApiResponse.success("Total paid amount calculated", response));
    }
    
    @GetMapping("/driver/{driverId}/pending")
    public ResponseEntity<ApiResponse<List<DriverPayoutResponseDto>>> getPendingPayoutsByDriver(
            @PathVariable Long driverId) {
        List<DriverPayoutResponseDto> response = payoutService.getPendingPayoutsByDriver(driverId);
        return ResponseEntity.ok(ApiResponse.success("Pending payouts retrieved", response));
    }
}