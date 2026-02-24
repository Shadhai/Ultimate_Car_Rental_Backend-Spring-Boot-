package com.carrental.service.localhead;

import com.carrental.dto.request.VehicleRequestDto;
import com.carrental.dto.response.VehicleResponseDto;
import com.carrental.entity.Location;
import com.carrental.entity.Vehicle;
import com.carrental.entity.User;
import com.carrental.enums.VehicleStatus;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.mapper.VehicleMapper;
import com.carrental.repository.LocationRepository;
import com.carrental.repository.VehicleRepository;
import com.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalHeadVehicleService {
    
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final VehicleMapper vehicleMapper;
    
    // Fix method signatures to match controller
    public List<VehicleResponseDto> getVehiclesByLocation(Long locationId, Long localHeadId) {
        User localHead = userRepository.findById(localHeadId)
                .orElseThrow(() -> new ResourceNotFoundException("Local head not found"));
        
        // Verify local head has authority over this location
        if (!localHead.getLocation().getId().equals(locationId)) {
            throw new BadRequestException("You are not authorized to view vehicles in this location");
        }
        
        List<Vehicle> vehicles = vehicleRepository.findByLocationId(locationId);
        return vehicles.stream()
                .map(vehicleMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<VehicleResponseDto> getAvailableVehicles(Long locationId, Long localHeadId) {
        User localHead = userRepository.findById(localHeadId)
                .orElseThrow(() -> new ResourceNotFoundException("Local head not found"));
        
        // Verify local head has authority over this location
        if (!localHead.getLocation().getId().equals(locationId)) {
            throw new BadRequestException("You are not authorized to view vehicles in this location");
        }
        
        return vehicleRepository.findAvailableVehiclesByLocation(locationId).stream()
                .map(vehicleMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public VehicleResponseDto addVehicleToLocation(VehicleRequestDto request, Long localHeadId) {
        User localHead = userRepository.findById(localHeadId)
                .orElseThrow(() -> new ResourceNotFoundException("Local head not found"));
        
        // Verify local head has authority over this location
        if (!localHead.getLocation().getId().equals(request.getLocationId())) {
            throw new BadRequestException("You are not authorized to add vehicles to this location");
        }
        
        // Check if registration number already exists
        if (vehicleRepository.findByRegistrationNumber(request.getRegistrationNumber()).isPresent()) {
            throw new BadRequestException("Registration number already exists");
        }
        
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
                .available(request.getStatus() == VehicleStatus.AVAILABLE)
                .build();
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(savedVehicle);
    }
    
    @Transactional
    public VehicleResponseDto updateVehicle(Long vehicleId, VehicleRequestDto request, Long localHeadId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        
        User localHead = userRepository.findById(localHeadId)
                .orElseThrow(() -> new ResourceNotFoundException("Local head not found"));
        
        // Verify local head has authority over vehicle's location
        if (!vehicle.getLocation().getId().equals(localHead.getLocation().getId())) {
            throw new BadRequestException("You are not authorized to update this vehicle");
        }
        
        // Check if registration number is being changed
        if (!request.getRegistrationNumber().equals(vehicle.getRegistrationNumber())) {
            if (vehicleRepository.findByRegistrationNumber(request.getRegistrationNumber()).isPresent()) {
                throw new BadRequestException("Registration number already exists");
            }
            vehicle.setRegistrationNumber(request.getRegistrationNumber());
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
        vehicle.setAvailable(request.getStatus() == VehicleStatus.AVAILABLE);
        
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(updatedVehicle);
    }
    
    @Transactional
    public void removeVehicle(Long vehicleId, Long localHeadId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        
        User localHead = userRepository.findById(localHeadId)
                .orElseThrow(() -> new ResourceNotFoundException("Local head not found"));
        
        // Verify local head has authority over vehicle's location
        if (!vehicle.getLocation().getId().equals(localHead.getLocation().getId())) {
            throw new BadRequestException("You are not authorized to remove this vehicle");
        }
        
        vehicleRepository.delete(vehicle);
    }
    
    @Transactional
    public VehicleResponseDto updateVehicleStatus(Long vehicleId, VehicleStatus status, Long localHeadId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        
        User localHead = userRepository.findById(localHeadId)
                .orElseThrow(() -> new ResourceNotFoundException("Local head not found"));
        
        // Verify local head has authority over vehicle's location
        if (!vehicle.getLocation().getId().equals(localHead.getLocation().getId())) {
            throw new BadRequestException("You are not authorized to update this vehicle");
        }
        
        vehicle.setStatus(status);
        vehicle.setAvailable(status == VehicleStatus.AVAILABLE);
        
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(updatedVehicle);
    }
    
    public VehicleResponseDto getVehicleDetails(Long vehicleId, Long localHeadId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        
        User localHead = userRepository.findById(localHeadId)
                .orElseThrow(() -> new ResourceNotFoundException("Local head not found"));
        
        // Verify local head has authority over vehicle's location
        if (!vehicle.getLocation().getId().equals(localHead.getLocation().getId())) {
            throw new BadRequestException("You are not authorized to view this vehicle");
        }
        
        return vehicleMapper.toResponseDto(vehicle);
    }
    
    public List<VehicleResponseDto> searchVehicles(String keyword, Long locationId, Long localHeadId) {
        User localHead = userRepository.findById(localHeadId)
                .orElseThrow(() -> new ResourceNotFoundException("Local head not found"));
        
        // Verify local head has authority over this location
        if (!localHead.getLocation().getId().equals(locationId)) {
            throw new BadRequestException("You are not authorized to search vehicles in this location");
        }
        
        List<Vehicle> vehicles = vehicleRepository.findByLocationId(locationId);
        
        return vehicles.stream()
                .filter(vehicle -> vehicle.getBrand().toLowerCase().contains(keyword.toLowerCase()) ||
                                  vehicle.getModel().toLowerCase().contains(keyword.toLowerCase()) ||
                                  vehicle.getRegistrationNumber().toLowerCase().contains(keyword.toLowerCase()))
                .map(vehicleMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}