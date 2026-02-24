package com.carrental.controller.customer;

import com.carrental.dto.request.PaymentRequestDto;
import com.carrental.dto.response.ApiResponse;
import com.carrental.dto.response.PaymentResponseDto;
import com.carrental.service.customer.CustomerPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/payments")
@RequiredArgsConstructor
public class CustomerPaymentController {
    
    private final CustomerPaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponseDto>> processPayment(
            @RequestHeader("X-User-Id") Long customerId,
            @Valid @RequestBody PaymentRequestDto request) {
        PaymentResponseDto response = paymentService.processPayment(customerId, request);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", response));
    }
    
    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> getPaymentByRental(
            @RequestHeader("X-User-Id") Long customerId,
            @PathVariable Long rentalId) {
        PaymentResponseDto response = paymentService.getPaymentByRentalId(rentalId, customerId);
        return ResponseEntity.ok(ApiResponse.success("Payment details retrieved", response));
    }
    
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> requestRefund(
            @RequestHeader("X-User-Id") Long customerId,
            @PathVariable Long paymentId) {
        PaymentResponseDto response = paymentService.refundPayment(paymentId, customerId);
        return ResponseEntity.ok(ApiResponse.success("Refund requested successfully", response));
    }
}