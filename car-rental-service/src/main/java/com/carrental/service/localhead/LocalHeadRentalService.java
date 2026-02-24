package com.carrental.service.localhead;

import com.carrental.dto.response.RentalResponseDto;
import com.carrental.entity.Rental;
import com.carrental.entity.User;
import com.carrental.enums.RentalStatus;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.mapper.RentalMapper;
import com.carrental.repository.RentalRepository;
import com.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalHeadRentalService {
    
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final RentalMapper rentalMapper;
    
    // ADD localHeadId parameter
    public List<RentalResponseDto> getRentalsByLocation(Long locationId, Long localHeadId) {
        User localHead = userRepository.findById(localHeadId)
                .orElseThrow(() -> new ResourceNotFoundException("Local head not found"));
        
        // Verify local head has authority over this location
        if (!localHead.getLocation().getId().equals(locationId)) {
            throw new BadRequestException("You are not authorized to view rentals in this location");
        }
        
        List<Rental> rentals = rentalRepository.findByVehicleLocationId(locationId);
        return rentals.stream()
                .map(rentalMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    // ADD localHeadId parameter
    public List<RentalResponseDto> getActiveRentalsByLocation(Long locationId, Long localHeadId) {
        User localHead = userRepository.findById(localHeadId)
                .orElseThrow(() -> new ResourceNotFoundException("Local head not found"));
        
        // Verify local head has authority over this location
        if (!localHead.getLocation().getId().equals(locationId)) {
            throw new BadRequestException("You are not authorized to view rentals in this location");
        }
        
        List<Rental> rentals = rentalRepository.findByVehicleLocationIdAndStatus(locationId, RentalStatus.ONGOING);
        return rentals.stream()
                .map(rentalMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    // This method is already correct
    @Transactional
    public RentalResponseDto assignDriver(Long rentalId, Long driverId, Long localHeadId) {
        User localHead = userRepository.findById(localHeadId)
                .orElseThrow(() -> new ResourceNotFoundException("Local head not found"));
        
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        
        // Verify local head has authority over rental's location
        if (!rental.getVehicle().getLocation().getId().equals(localHead.getLocation().getId())) {
            throw new BadRequestException("You are not authorized to modify this rental");
        }
        
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        // Verify driver is assigned to same location
        if (!driver.getLocation().getId().equals(localHead.getLocation().getId())) {
            throw new BadRequestException("Driver is not assigned to your location");
        }
        
        rental.setDriver(driver);
        rental.setStatus(RentalStatus.CONFIRMED);
        Rental updatedRental = rentalRepository.save(rental);
        
        return rentalMapper.toResponseDto(updatedRental);
    }
    
    // This method is already correct
    @Transactional
    public RentalResponseDto updateRentalStatus(Long rentalId, RentalStatus status, Long localHeadId) {
        User localHead = userRepository.findById(localHeadId)
                .orElseThrow(() -> new ResourceNotFoundException("Local head not found"));
        
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        
        // Verify local head has authority over rental's location
        if (!rental.getVehicle().getLocation().getId().equals(localHead.getLocation().getId())) {
            throw new BadRequestException("You are not authorized to modify this rental");
        }
        
        rental.setStatus(status);
        Rental updatedRental = rentalRepository.save(rental);
        
        return rentalMapper.toResponseDto(updatedRental);
    }
    
    // This method is already correct
    public List<RentalResponseDto> getRentalsByDriver(Long driverId, Long localHeadId) {
        User localHead = userRepository.findById(localHeadId)
                .orElseThrow(() -> new ResourceNotFoundException("Local head not found"));
        
        List<Rental> rentals = rentalRepository.findByDriverId(driverId);
        
        // Filter rentals that belong to local head's location
        List<Rental> filteredRentals = rentals.stream()
                .filter(rental -> rental.getVehicle().getLocation().getId().equals(localHead.getLocation().getId()))
                .collect(Collectors.toList());
        
        return filteredRentals.stream()
                .map(rentalMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}