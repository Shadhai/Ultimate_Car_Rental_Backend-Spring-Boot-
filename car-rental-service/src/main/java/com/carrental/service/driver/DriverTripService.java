package com.carrental.service.driver;

import com.carrental.dto.response.RentalResponseDto;
import com.carrental.entity.Rental;
import com.carrental.entity.User;
import com.carrental.entity.Vehicle;
import com.carrental.enums.RentalStatus;
import com.carrental.enums.VehicleStatus;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.mapper.RentalMapper;
import com.carrental.repository.RentalRepository;
import com.carrental.repository.UserRepository;
import com.carrental.repository.VehicleRepository;
import com.carrental.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverTripService extends BaseService<Rental, Long> {
    
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalMapper rentalMapper;
    
    @Override
    protected RentalRepository getRepository() {
        return rentalRepository;
    }
    
    @Override
    protected String getEntityName() {
        return "Rental";
    }
    
    @Transactional
    public RentalResponseDto startTrip(Long rentalId, Long driverId) {
        Rental rental = findById(rentalId);
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        // Authorization check
        if (rental.getDriver() == null || !rental.getDriver().getId().equals(driverId)) {
            throw new BadRequestException("You are not assigned to this rental");
        }
        
        // Status validation
        if (rental.getStatus() != RentalStatus.CONFIRMED) {
            throw new BadRequestException("Rental must be confirmed before starting trip");
        }
        
        if (rental.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Cannot start trip before scheduled start time");
        }
        
        if (rental.getActualStartTime() != null) {
            throw new BadRequestException("Trip already started");
        }
        
        // Update rental
        rental.setStatus(RentalStatus.ONGOING);
        rental.setActualStartTime(LocalDateTime.now());
        
        // Update vehicle status
        Vehicle vehicle = rental.getVehicle();
        vehicle.setStatus(VehicleStatus.RENTED);
        vehicleRepository.save(vehicle);
        
        Rental updatedRental = rentalRepository.save(rental);
        return rentalMapper.toResponseDto(updatedRental);
    }
    
    @Transactional
    public RentalResponseDto completeTrip(Long rentalId, Long driverId) {
        Rental rental = findById(rentalId);
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        // Authorization check
        if (rental.getDriver() == null || !rental.getDriver().getId().equals(driverId)) {
            throw new BadRequestException("You are not assigned to this rental");
        }
        
        // Status validation
        if (rental.getStatus() != RentalStatus.ONGOING) {
            throw new BadRequestException("Rental must be ongoing to complete trip");
        }
        
        if (rental.getActualStartTime() == null) {
            throw new BadRequestException("Trip not started yet");
        }
        
        // Update rental
        rental.setStatus(RentalStatus.COMPLETED);
        rental.setActualEndTime(LocalDateTime.now());
        
        // Update vehicle status
        Vehicle vehicle = rental.getVehicle();
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setAvailable(true);
        vehicleRepository.save(vehicle);
        
        Rental updatedRental = rentalRepository.save(rental);
        return rentalMapper.toResponseDto(updatedRental);
    }
    
    @Transactional
    public RentalResponseDto cancelTrip(Long rentalId, Long driverId, String reason) {
        Rental rental = findById(rentalId);
        
        // Authorization check
        if (rental.getDriver() == null || !rental.getDriver().getId().equals(driverId)) {
            throw new BadRequestException("You are not assigned to this rental");
        }
        
        // Status validation
        if (rental.getStatus() != RentalStatus.ONGOING) {
            throw new BadRequestException("Only ongoing trips can be cancelled");
        }
        
        // Update rental
        rental.setStatus(RentalStatus.CANCELLED);
        rental.setActualEndTime(LocalDateTime.now());
        rental.setNotes((rental.getNotes() != null ? rental.getNotes() + "\n" : "") + 
                       "Driver cancelled: " + reason);
        
        // Update vehicle status
        Vehicle vehicle = rental.getVehicle();
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setAvailable(true);
        vehicleRepository.save(vehicle);
        
        Rental updatedRental = rentalRepository.save(rental);
        return rentalMapper.toResponseDto(updatedRental);
    }
    
    public List<RentalResponseDto> getAssignedTrips(Long driverId) {
        List<Rental> rentals = rentalRepository.findByDriverId(driverId);
        return rentals.stream()
                .map(rentalMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<RentalResponseDto> getActiveTrips(Long driverId) {
        List<Rental> rentals = rentalRepository.findByDriverId(driverId);
        return rentals.stream()
                .filter(r -> r.getStatus() == RentalStatus.ONGOING)
                .map(rentalMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<RentalResponseDto> getUpcomingTrips(Long driverId) {
        List<Rental> rentals = rentalRepository.findByDriverId(driverId);
        return rentals.stream()
                .filter(r -> r.getStatus() == RentalStatus.CONFIRMED)
                .filter(r -> r.getStartTime().isAfter(LocalDateTime.now()))
                .map(rentalMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public RentalResponseDto getTripDetails(Long rentalId, Long driverId) {
        Rental rental = findById(rentalId);
        
        // Authorization check
        if (rental.getDriver() == null || !rental.getDriver().getId().equals(driverId)) {
            throw new BadRequestException("You are not authorized to view this trip");
        }
        
        return rentalMapper.toResponseDto(rental);
    }
}