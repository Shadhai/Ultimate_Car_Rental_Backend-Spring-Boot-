package com.carrental.controller.localhead;

import com.carrental.dto.request.VehicleRequestDto;
import com.carrental.dto.response.ApiResponse;
import com.carrental.dto.response.VehicleResponseDto;
import com.carrental.enums.VehicleStatus;
import com.carrental.service.localhead.LocalHeadVehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/local-head/vehicles")
@RequiredArgsConstructor
public class LocalHeadVehicleController {
    
    private final LocalHeadVehicleService vehicleService;
    
    @GetMapping("/location/{locationId}")
    public ResponseEntity<ApiResponse<List<VehicleResponseDto>>> getVehiclesByLocation(
            @RequestHeader("X-User-Id") Long localHeadId,
            @PathVariable Long locationId) {
        List<VehicleResponseDto> response = vehicleService.getVehiclesByLocation(locationId, localHeadId);
        return ResponseEntity.ok(ApiResponse.success("Vehicles retrieved successfully", response));
    }
    
    @GetMapping("/location/{locationId}/available")
    public ResponseEntity<ApiResponse<List<VehicleResponseDto>>> getAvailableVehicles(
            @RequestHeader("X-User-Id") Long localHeadId,
            @PathVariable Long locationId) {
        List<VehicleResponseDto> response = vehicleService.getAvailableVehicles(locationId, localHeadId);
        return ResponseEntity.ok(ApiResponse.success("Available vehicles retrieved", response));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponseDto>> addVehicle(
            @RequestHeader("X-User-Id") Long localHeadId,
            @Valid @RequestBody VehicleRequestDto request) {
        VehicleResponseDto response = vehicleService.addVehicleToLocation(request, localHeadId);
        return ResponseEntity.ok(ApiResponse.success("Vehicle added successfully", response));
    }
    
    @PutMapping("/{vehicleId}")
    public ResponseEntity<ApiResponse<VehicleResponseDto>> updateVehicle(
            @RequestHeader("X-User-Id") Long localHeadId,
            @PathVariable Long vehicleId,
            @Valid @RequestBody VehicleRequestDto request) {
        VehicleResponseDto response = vehicleService.updateVehicle(vehicleId, request, localHeadId);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated successfully", response));
    }
    
    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<ApiResponse<Void>> removeVehicle(
            @RequestHeader("X-User-Id") Long localHeadId,
            @PathVariable Long vehicleId) {
        vehicleService.removeVehicle(vehicleId, localHeadId);
        return ResponseEntity.ok(ApiResponse.success("Vehicle removed successfully", null));
    }
    
    @PutMapping("/{vehicleId}/status")
    public ResponseEntity<ApiResponse<VehicleResponseDto>> updateVehicleStatus(
            @RequestHeader("X-User-Id") Long localHeadId,
            @PathVariable Long vehicleId,
            @RequestParam VehicleStatus status) {
        VehicleResponseDto response = vehicleService.updateVehicleStatus(vehicleId, status, localHeadId);
        return ResponseEntity.ok(ApiResponse.success("Vehicle status updated", response));
    }
    
    @GetMapping("/{vehicleId}")
    public ResponseEntity<ApiResponse<VehicleResponseDto>> getVehicleDetails(
            @RequestHeader("X-User-Id") Long localHeadId,
            @PathVariable Long vehicleId) {
        VehicleResponseDto response = vehicleService.getVehicleDetails(vehicleId, localHeadId);
        return ResponseEntity.ok(ApiResponse.success("Vehicle details retrieved", response));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<VehicleResponseDto>>> searchVehicles(
            @RequestHeader("X-User-Id") Long localHeadId,
            @RequestParam String keyword,
            @RequestParam Long locationId) {
        List<VehicleResponseDto> response = vehicleService.searchVehicles(keyword, locationId, localHeadId);
        return ResponseEntity.ok(ApiResponse.success("Search results", response));
    }
}