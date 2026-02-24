package com.carrental.controller.driver;

import com.carrental.dto.response.ApiResponse;
import com.carrental.dto.response.DriverStatusDto;
import com.carrental.enums.UserStatus;
import com.carrental.service.driver.DriverStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver/status")
@RequiredArgsConstructor
public class DriverStatusController {
    
    private final DriverStatusService statusService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<DriverStatusDto>> getDriverStatus(
            @RequestHeader("X-User-Id") Long driverId) {
        DriverStatusDto response = statusService.getDriverStatus(driverId);
        return ResponseEntity.ok(ApiResponse.success("Driver status retrieved", response));
    }
    
    @PutMapping
    public ResponseEntity<ApiResponse<DriverStatusDto>> updateDriverStatus(
            @RequestHeader("X-User-Id") Long driverId,
            @RequestParam UserStatus status) {
        DriverStatusDto response = statusService.updateDriverStatus(driverId, status);
        return ResponseEntity.ok(ApiResponse.success("Driver status updated", response));
    }
    
    @PostMapping("/online")
    public ResponseEntity<ApiResponse<DriverStatusDto>> goOnline(
            @RequestHeader("X-User-Id") Long driverId,
            @RequestParam String location) {
        DriverStatusDto response = statusService.goOnline(driverId, location);
        return ResponseEntity.ok(ApiResponse.success("Driver is now online", response));
    }
    
    @PostMapping("/offline")
    public ResponseEntity<ApiResponse<DriverStatusDto>> goOffline(
            @RequestHeader("X-User-Id") Long driverId) {
        DriverStatusDto response = statusService.goOffline(driverId);
        return ResponseEntity.ok(ApiResponse.success("Driver is now offline", response));
    }
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<Boolean>> isDriverAvailable(
            @RequestHeader("X-User-Id") Long driverId) {
        boolean response = statusService.isDriverAvailable(driverId);
        return ResponseEntity.ok(ApiResponse.success("Availability status checked", response));
    }
}