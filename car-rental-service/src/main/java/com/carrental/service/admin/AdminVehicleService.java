package com.carrental.service.admin;

import com.carrental.dto.request.VehicleRequestDto;
import com.carrental.dto.response.VehicleResponseDto;
import com.carrental.entity.Location;
import com.carrental.entity.Vehicle;
import com.carrental.enums.RentalStatus;
import com.carrental.enums.VehicleStatus;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.mapper.VehicleMapper;
import com.carrental.repository.LocationRepository;
import com.carrental.repository.RentalRepository;
import com.carrental.repository.VehicleRepository;
import com.carrental.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminVehicleService extends BaseService<Vehicle, Long> {
    
    private final VehicleRepository vehicleRepository;
    private final LocationRepository locationRepository;
    private final VehicleMapper vehicleMapper;
    private final RentalRepository rentalRepository;     
    @Override
    protected VehicleRepository getRepository() {
        return vehicleRepository;
    }
    
    @Override
    protected String getEntityName() {
        return "Vehicle";
    }
    
    @Transactional
    public VehicleResponseDto createVehicle(VehicleRequestDto request) {
        // Check if registration number already exists
        if (vehicleRepository.findByRegistrationNumber(request.getRegistrationNumber()).isPresent()) {
            throw new BadRequestException("Registration number already exists");
        }
        
        // Get location
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        
        // Create vehicle
        Vehicle vehicle = Vehicle.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .registrationNumber(request.getRegistrationNumber())
                .year(request.getYear())
                .type(request.getType())
                .status(request.getStatus())
                .dailyRate(request.getDailyRate())
                .hourlyRate(request.getHourlyRate())
                .location(location)
                .capacity(request.getCapacity())
                .color(request.getColor())
                .fuelType(request.getFuelType())
                .features(request.getFeatures())
                .insuranceNumber(request.getInsuranceNumber())
                .available(true)
                .build();
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(savedVehicle);
    }
    
    @Transactional
    public VehicleResponseDto updateVehicle(Long id, VehicleRequestDto request) {
        Vehicle vehicle = findById(id);
        
        // Check if registration number is being changed
        if (!request.getRegistrationNumber().equals(vehicle.getRegistrationNumber())) {
            if (vehicleRepository.findByRegistrationNumber(request.getRegistrationNumber()).isPresent()) {
                throw new BadRequestException("Registration number already exists");
            }
            vehicle.setRegistrationNumber(request.getRegistrationNumber());
        }
        
        // Update location if changed
        if (!request.getLocationId().equals(vehicle.getLocation().getId())) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
            vehicle.setLocation(location);
        }
        
        // Update other fields
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setType(request.getType());
        vehicle.setStatus(request.getStatus());
        vehicle.setDailyRate(request.getDailyRate());
        vehicle.setHourlyRate(request.getHourlyRate());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setColor(request.getColor());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setFeatures(request.getFeatures());
        vehicle.setInsuranceNumber(request.getInsuranceNumber());
        
        // Update availability based on status
        vehicle.setAvailable(request.getStatus() == VehicleStatus.AVAILABLE);
        
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(updatedVehicle);
    }
    
    public List<VehicleResponseDto> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public Page<VehicleResponseDto> getVehiclesByPage(Pageable pageable) {
        return vehicleRepository.findAll(pageable)
                .map(vehicleMapper::toResponseDto);
    }
    
    public List<VehicleResponseDto> getAvailableVehicles() {
        return vehicleRepository.findByAvailable(true).stream()
                .map(vehicleMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<VehicleResponseDto> getVehiclesByLocation(Long locationId) {
        return vehicleRepository.findByLocationId(locationId).stream()
                .map(vehicleMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<VehicleResponseDto> getVehiclesByStatus(VehicleStatus status) {
        return vehicleRepository.findByStatus(status).stream()
                .map(vehicleMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public VehicleResponseDto updateVehicleStatus(Long id, VehicleStatus status) {
        Vehicle vehicle = findById(id);
        vehicle.setStatus(status);
        vehicle.setAvailable(status == VehicleStatus.AVAILABLE);
        
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(updatedVehicle);
    }
    
    @Transactional
    public void scheduleMaintenance(Long vehicleId, String description) {
        Vehicle vehicle = findById(vehicleId);
        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        vehicle.setAvailable(false);
        vehicleRepository.save(vehicle);
        
        // Here you would create a maintenance record
        // maintenanceRepository.save(new Maintenance(...));
    }
    
    @Transactional
    public void completeMaintenance(Long vehicleId) {
        Vehicle vehicle = findById(vehicleId);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setAvailable(true);
        vehicle.setLastMaintenanceDate(java.time.LocalDate.now());
        vehicleRepository.save(vehicle);
    }
    
    public Long countAvailableVehicles() {
        return vehicleRepository.countAvailableVehicles();
    }
    
    public List<VehicleResponseDto> searchVehicles(String keyword) {
        // This would require a custom query
        return vehicleRepository.findAll().stream()
                .filter(vehicle -> vehicle.getBrand().toLowerCase().contains(keyword.toLowerCase()) ||
                                  vehicle.getModel().toLowerCase().contains(keyword.toLowerCase()) ||
                                  vehicle.getRegistrationNumber().toLowerCase().contains(keyword.toLowerCase()))
                .map(vehicleMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    // Add this method
    public VehicleResponseDto getVehicleById(Long id) {
        Vehicle vehicle = findById(id);
        return vehicleMapper.toResponseDto(vehicle);
    }
    
    // Add this method
    
    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = findById(id);
        
        // Check if vehicle has active or pending rentals
        boolean hasActiveRentals = rentalRepository.existsByVehicleIdAndStatusIn(
            id, 
            Arrays.asList(RentalStatus.CONFIRMED, RentalStatus.ONGOING, RentalStatus.PENDING)
        );
        
        if (hasActiveRentals) {
            throw new BadRequestException("Cannot delete vehicle with active or pending rentals");
        }
        
        vehicleRepository.delete(vehicle);
}
}