package com.carrental.service.driver;

import com.carrental.dto.response.DriverStatusDto;
import com.carrental.entity.User;
import com.carrental.enums.UserStatus;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverStatusService {
    
    private final UserRepository userRepository;
    
    public DriverStatusDto getDriverStatus(Long driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        // Check if user is actually a driver
        if (driver.getRole() != com.carrental.enums.Role.DRIVER) {
            throw new BadRequestException("User is not a driver");
        }
        
        return DriverStatusDto.builder()
                .driverId(driverId)
                .driverName(driver.getName())
                .status(driver.getStatus())
                .isActive(driver.isActive())
                .lastActive(LocalDateTime.now()) // This should come from tracking
                .currentLocation("Not tracked") // This should come from GPS tracking
                .isAvailable(driver.getStatus() == UserStatus.ACTIVE && driver.isActive())
                .build();
    }
    
    @Transactional
    public DriverStatusDto updateDriverStatus(Long driverId, UserStatus status) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        // Check if user is actually a driver
        if (driver.getRole() != com.carrental.enums.Role.DRIVER) {
            throw new BadRequestException("User is not a driver");
        }
        
        driver.setStatus(status);
        driver.setActive(status == UserStatus.ACTIVE);
        
        User updatedDriver = userRepository.save(driver);
        
        return DriverStatusDto.builder()
                .driverId(driverId)
                .driverName(updatedDriver.getName())
                .status(updatedDriver.getStatus())
                .isActive(updatedDriver.isActive())
                .lastActive(LocalDateTime.now())
                .currentLocation("Not tracked")
                .isAvailable(updatedDriver.getStatus() == UserStatus.ACTIVE && updatedDriver.isActive())
                .build();
    }
    
    @Transactional
    public DriverStatusDto goOnline(Long driverId, String currentLocation) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        // Check if user is actually a driver
        if (driver.getRole() != com.carrental.enums.Role.DRIVER) {
            throw new BadRequestException("User is not a driver");
        }
        
        // Check if driver is active
        if (!driver.isActive() || driver.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("Driver account is not active");
        }
        
        // Here you would update driver's location in a separate tracking service
        // For now, just update the status
        
        return DriverStatusDto.builder()
                .driverId(driverId)
                .driverName(driver.getName())
                .status(driver.getStatus())
                .isActive(driver.isActive())
                .lastActive(LocalDateTime.now())
                .currentLocation(currentLocation)
                .isAvailable(true)
                .onlineSince(LocalDateTime.now())
                .build();
    }
    
    @Transactional
    public DriverStatusDto goOffline(Long driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        // Check if user is actually a driver
        if (driver.getRole() != com.carrental.enums.Role.DRIVER) {
            throw new BadRequestException("User is not a driver");
        }
        
        return DriverStatusDto.builder()
                .driverId(driverId)
                .driverName(driver.getName())
                .status(driver.getStatus())
                .isActive(driver.isActive())
                .lastActive(LocalDateTime.now())
                .currentLocation("Offline")
                .isAvailable(false)
                .build();
    }
    
    public boolean isDriverAvailable(Long driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        return driver.getRole() == com.carrental.enums.Role.DRIVER &&
               driver.isActive() &&
               driver.getStatus() == UserStatus.ACTIVE;
    }
}