package com.carrental.controller.localhead;

import com.carrental.dto.response.ApiResponse;
import com.carrental.dto.response.RentalResponseDto;
import com.carrental.enums.RentalStatus;
import com.carrental.service.localhead.LocalHeadRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/local-head/rentals")
@RequiredArgsConstructor
public class LocalHeadRentalController {
    
    private final LocalHeadRentalService rentalService;
    
    @GetMapping("/location/{locationId}")
    public ResponseEntity<ApiResponse<List<RentalResponseDto>>> getRentalsByLocation(
            @RequestHeader("X-User-Id") Long localHeadId,
            @PathVariable Long locationId) {
        List<RentalResponseDto> response = rentalService.getRentalsByLocation(locationId, localHeadId); // ADD localHeadId
        return ResponseEntity.ok(ApiResponse.success("Rentals retrieved successfully", response));
    }
    
    @GetMapping("/location/{locationId}/active")
    public ResponseEntity<ApiResponse<List<RentalResponseDto>>> getActiveRentalsByLocation(
            @RequestHeader("X-User-Id") Long localHeadId,
            @PathVariable Long locationId) {
        List<RentalResponseDto> response = rentalService.getActiveRentalsByLocation(locationId, localHeadId); // ADD localHeadId
        return ResponseEntity.ok(ApiResponse.success("Active rentals retrieved", response));
    }
    
    @PostMapping("/{rentalId}/assign-driver")
    public ResponseEntity<ApiResponse<RentalResponseDto>> assignDriver(
            @RequestHeader("X-User-Id") Long localHeadId,
            @PathVariable Long rentalId,
            @RequestParam Long driverId) {
        RentalResponseDto response = rentalService.assignDriver(rentalId, driverId, localHeadId);
        return ResponseEntity.ok(ApiResponse.success("Driver assigned successfully", response));
    }
    
    @PutMapping("/{rentalId}/status")
    public ResponseEntity<ApiResponse<RentalResponseDto>> updateRentalStatus(
            @RequestHeader("X-User-Id") Long localHeadId,
            @PathVariable Long rentalId,
            @RequestParam RentalStatus status) {
        RentalResponseDto response = rentalService.updateRentalStatus(rentalId, status, localHeadId);
        return ResponseEntity.ok(ApiResponse.success("Rental status updated", response));
    }
    
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<RentalResponseDto>>> getRentalsByDriver(
            @RequestHeader("X-User-Id") Long localHeadId,
            @PathVariable Long driverId) {
        List<RentalResponseDto> response = rentalService.getRentalsByDriver(driverId, localHeadId);
        return ResponseEntity.ok(ApiResponse.success("Driver rentals retrieved", response));
    }
}