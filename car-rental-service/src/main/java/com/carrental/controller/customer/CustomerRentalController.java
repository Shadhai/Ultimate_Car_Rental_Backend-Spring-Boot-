package com.carrental.controller.customer;

import com.carrental.dto.request.RentalRequestDto;
import com.carrental.dto.response.ApiResponse;
import com.carrental.dto.response.RentalResponseDto;
import com.carrental.service.customer.CustomerRentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/rentals")
@RequiredArgsConstructor
public class CustomerRentalController {
    
    private final CustomerRentalService rentalService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<RentalResponseDto>> createRental(
            @RequestHeader("X-User-Id") Long customerId,
            @Valid @RequestBody RentalRequestDto request) {
        RentalResponseDto response = rentalService.createRental(customerId, request);
        return ResponseEntity.ok(ApiResponse.success("Rental created successfully", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<RentalResponseDto>>> getCustomerRentals(
            @RequestHeader("X-User-Id") Long customerId) {
        List<RentalResponseDto> response = rentalService.getCustomerRentals(customerId);
        return ResponseEntity.ok(ApiResponse.success("Rentals retrieved successfully", response));
    }
    
    @GetMapping("/{rentalId}")
    public ResponseEntity<ApiResponse<RentalResponseDto>> getRentalById(
            @RequestHeader("X-User-Id") Long customerId,
            @PathVariable Long rentalId) {
        RentalResponseDto response = rentalService.getRentalById(rentalId, customerId);
        return ResponseEntity.ok(ApiResponse.success("Rental details retrieved", response));
    }
    
    @PostMapping("/{rentalId}/cancel")
    public ResponseEntity<ApiResponse<RentalResponseDto>> cancelRental(
            @RequestHeader("X-User-Id") Long customerId,
            @PathVariable Long rentalId) {
        RentalResponseDto response = rentalService.cancelRental(rentalId, customerId);
        return ResponseEntity.ok(ApiResponse.success("Rental cancelled successfully", response));
    }
}