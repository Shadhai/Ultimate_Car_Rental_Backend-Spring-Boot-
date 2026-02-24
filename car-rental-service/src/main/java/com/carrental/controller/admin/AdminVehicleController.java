package com.carrental.controller.admin;

import com.carrental.dto.request.VehicleRequestDto;
import com.carrental.dto.response.ApiResponse;
import com.carrental.dto.response.VehicleResponseDto;
import com.carrental.enums.VehicleStatus;
import com.carrental.service.admin.AdminVehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vehicles")
@RequiredArgsConstructor
public class AdminVehicleController {
    
    private final AdminVehicleService vehicleService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponseDto>> createVehicle(
            @Valid @RequestBody VehicleRequestDto request) {
        VehicleResponseDto response = vehicleService.createVehicle(request);
        return ResponseEntity.ok(ApiResponse.success("Vehicle created successfully", response));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponseDto>> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequestDto request) {
        VehicleResponseDto response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated successfully", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleResponseDto>>> getAllVehicles() {
        List<VehicleResponseDto> response = vehicleService.getAllVehicles();
        return ResponseEntity.ok(ApiResponse.success("Vehicles retrieved successfully", response));
    }
    
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<VehicleResponseDto>>> getVehiclesByPage(Pageable pageable) {
        Page<VehicleResponseDto> response = vehicleService.getVehiclesByPage(pageable);
        return ResponseEntity.ok(ApiResponse.success("Vehicles page retrieved", response));
    }
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<VehicleResponseDto>>> getAvailableVehicles() {
        List<VehicleResponseDto> response = vehicleService.getAvailableVehicles();
        return ResponseEntity.ok(ApiResponse.success("Available vehicles retrieved", response));
    }
    
    @GetMapping("/location/{locationId}")
    public ResponseEntity<ApiResponse<List<VehicleResponseDto>>> getVehiclesByLocation(@PathVariable Long locationId) {
        List<VehicleResponseDto> response = vehicleService.getVehiclesByLocation(locationId);
        return ResponseEntity.ok(ApiResponse.success("Vehicles by location retrieved", response));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<VehicleResponseDto>>> getVehiclesByStatus(@PathVariable VehicleStatus status) {
        List<VehicleResponseDto> response = vehicleService.getVehiclesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Vehicles by status retrieved", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponseDto>> getVehicleById(@PathVariable Long id) {
        VehicleResponseDto response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle details retrieved", response));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VehicleResponseDto>> updateVehicleStatus(
            @PathVariable Long id,
            @RequestParam VehicleStatus status) {
        VehicleResponseDto response = vehicleService.updateVehicleStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Vehicle status updated", response));
    }
    
    @PostMapping("/{id}/maintenance")
    public ResponseEntity<ApiResponse<Void>> scheduleMaintenance(
            @PathVariable Long id,
            @RequestParam String description) {
        vehicleService.scheduleMaintenance(id, description);
        return ResponseEntity.ok(ApiResponse.success("Maintenance scheduled", null));
    }
    
    @PostMapping("/{id}/maintenance/complete")
    public ResponseEntity<ApiResponse<Void>> completeMaintenance(@PathVariable Long id) {
        vehicleService.completeMaintenance(id);
        return ResponseEntity.ok(ApiResponse.success("Maintenance completed", null));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle deleted successfully", null));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<VehicleResponseDto>>> searchVehicles(@RequestParam String keyword) {
        List<VehicleResponseDto> response = vehicleService.searchVehicles(keyword);
        return ResponseEntity.ok(ApiResponse.success("Search results", response));
    }
}