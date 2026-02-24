package com.carrental.controller.driver;

import com.carrental.dto.response.ApiResponse;
import com.carrental.dto.response.RentalResponseDto;
import com.carrental.service.driver.DriverTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver/trips")
@RequiredArgsConstructor
public class DriverTripController {
    
    private final DriverTripService tripService;
    
    @PostMapping("/{rentalId}/start")
    public ResponseEntity<ApiResponse<RentalResponseDto>> startTrip(
            @RequestHeader("X-User-Id") Long driverId,
            @PathVariable Long rentalId) {
        RentalResponseDto response = tripService.startTrip(rentalId, driverId);
        return ResponseEntity.ok(ApiResponse.success("Trip started successfully", response));
    }
    
    @PostMapping("/{rentalId}/complete")
    public ResponseEntity<ApiResponse<RentalResponseDto>> completeTrip(
            @RequestHeader("X-User-Id") Long driverId,
            @PathVariable Long rentalId) {
        RentalResponseDto response = tripService.completeTrip(rentalId, driverId);
        return ResponseEntity.ok(ApiResponse.success("Trip completed successfully", response));
    }
    
    @PostMapping("/{rentalId}/cancel")
    public ResponseEntity<ApiResponse<RentalResponseDto>> cancelTrip(
            @RequestHeader("X-User-Id") Long driverId,
            @PathVariable Long rentalId,
            @RequestParam String reason) {
        RentalResponseDto response = tripService.cancelTrip(rentalId, driverId, reason);
        return ResponseEntity.ok(ApiResponse.success("Trip cancelled successfully", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<RentalResponseDto>>> getAssignedTrips(
            @RequestHeader("X-User-Id") Long driverId) {
        List<RentalResponseDto> response = tripService.getAssignedTrips(driverId);
        return ResponseEntity.ok(ApiResponse.success("Trips retrieved successfully", response));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<RentalResponseDto>>> getActiveTrips(
            @RequestHeader("X-User-Id") Long driverId) {
        List<RentalResponseDto> response = tripService.getActiveTrips(driverId);
        return ResponseEntity.ok(ApiResponse.success("Active trips retrieved", response));
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<RentalResponseDto>>> getUpcomingTrips(
            @RequestHeader("X-User-Id") Long driverId) {
        List<RentalResponseDto> response = tripService.getUpcomingTrips(driverId);
        return ResponseEntity.ok(ApiResponse.success("Upcoming trips retrieved", response));
    }
    
    @GetMapping("/{rentalId}")
    public ResponseEntity<ApiResponse<RentalResponseDto>> getTripDetails(
            @RequestHeader("X-User-Id") Long driverId,
            @PathVariable Long rentalId) {
        RentalResponseDto response = tripService.getTripDetails(rentalId, driverId);
        return ResponseEntity.ok(ApiResponse.success("Trip details retrieved", response));
    }
}