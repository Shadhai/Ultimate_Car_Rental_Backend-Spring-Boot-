package com.carrental.service.customer;

import com.carrental.dto.request.RentalRequestDto;
import com.carrental.dto.response.RentalResponseDto;
import com.carrental.entity.*;
import com.carrental.enums.RentalStatus;
import com.carrental.enums.VehicleStatus;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.mapper.RentalMapper;
import com.carrental.repository.*;
import com.carrental.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerRentalService extends BaseService<Rental, Long> {
    
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
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
    public RentalResponseDto createRental(Long customerId, RentalRequestDto request) {
        // Validate customer
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (!customer.isActive()) {
            throw new BadRequestException("Customer account is deactivated");
        }
        
        // Validate vehicle
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new BadRequestException("Vehicle is not available for rental");
        }
        
        if (!vehicle.isAvailable()) {
            throw new BadRequestException("Vehicle is currently rented");
        }
        
        // Validate rental dates
        LocalDateTime now = LocalDateTime.now();
        if (request.getStartTime().isBefore(now)) {
            throw new BadRequestException("Start time must be in the future");
        }
        
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }
        
        Duration duration = Duration.between(request.getStartTime(), request.getEndTime());
        if (duration.toHours() < 1) {
            throw new BadRequestException("Minimum rental duration is 1 hour");
        }
        
        // Check for overlapping rentals
        List<Rental> overlappingRentals = rentalRepository.findOverlappingRentals(
                request.getVehicleId(),
                request.getStartTime(),
                request.getEndTime()
        );
        
        if (!overlappingRentals.isEmpty()) {
            throw new BadRequestException("Vehicle is already booked for the selected time period");
        }
        
        // Calculate rental price
        BigDecimal totalFare = calculateRentalPrice(vehicle, request.getStartTime(), request.getEndTime());
        
        // Apply discount if coupon code provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            discountAmount = applyCouponDiscount(request.getCouponCode(), totalFare);
            totalFare = totalFare.subtract(discountAmount);
        }
        
        // Create rental
        Rental rental = Rental.builder()
                .customer(customer)
                .vehicle(vehicle)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .totalFare(totalFare)
                .status(RentalStatus.PENDING)
                .pickupLocation(request.getPickupLocation())
                .dropoffLocation(request.getDropoffLocation())
                .notes(request.getNotes())
                .paid(false)
                .build();
        
        // Update vehicle status
        vehicle.setStatus(VehicleStatus.RESERVED);
        vehicle.setAvailable(false);
        vehicleRepository.save(vehicle);
        
        Rental savedRental = rentalRepository.save(rental);
        
        return rentalMapper.toResponseDto(savedRental);
    }
    
    private BigDecimal calculateRentalPrice(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        long hours = duration.toHours();
        long days = duration.toDays();
        
        BigDecimal hourlyRate = vehicle.getHourlyRate() != null ? 
                vehicle.getHourlyRate() : 
                vehicle.getDailyRate().divide(BigDecimal.valueOf(24), 2, BigDecimal.ROUND_HALF_UP);
        
        if (hours <= 24) {
            // Hourly rental
            return hourlyRate.multiply(BigDecimal.valueOf(hours));
        } else {
            // Daily rental (with possible hourly remainder)
            long fullDays = days;
            long remainingHours = hours % 24;
            
            BigDecimal dailyCost = vehicle.getDailyRate().multiply(BigDecimal.valueOf(fullDays));
            BigDecimal hourlyCost = hourlyRate.multiply(BigDecimal.valueOf(remainingHours));
            
            return dailyCost.add(hourlyCost);
        }
    }
    
    private BigDecimal applyCouponDiscount(String couponCode, BigDecimal totalFare) {
        LocalDateTime now = LocalDateTime.now();
        return offerRepository.findValidOffer(couponCode, now)
                .map(offer -> {
                    BigDecimal discount = BigDecimal.ZERO;
                    
                    if (offer.getDiscountPercentage() != null) {
                        discount = totalFare.multiply(offer.getDiscountPercentage())
                                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                    } else if (offer.getDiscountAmount() != null) {
                        discount = offer.getDiscountAmount();
                    }
                    
                    // Apply max discount limit
                    if (offer.getMaxDiscountAmount() != null && 
                        discount.compareTo(offer.getMaxDiscountAmount()) > 0) {
                        discount = offer.getMaxDiscountAmount();
                    }
                    
                    // Update offer usage
                    offer.setUsedCount(offer.getUsedCount() + 1);
                    if (offer.getUsageLimit() != null && 
                        offer.getUsedCount() >= offer.getUsageLimit()) {
                        offer.setActive(false);
                    }
                    offerRepository.save(offer);
                    
                    return discount;
                })
                .orElseThrow(() -> new BadRequestException("Invalid or expired coupon code"));
    }
    
    @Transactional
    public RentalResponseDto cancelRental(Long rentalId, Long customerId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        
        // Check authorization
        if (!rental.getCustomer().getId().equals(customerId)) {
            throw new BadRequestException("You are not authorized to cancel this rental");
        }
        
        // Check if rental can be cancelled
        if (rental.getStatus() != RentalStatus.PENDING && 
            rental.getStatus() != RentalStatus.CONFIRMED) {
            throw new BadRequestException("Rental cannot be cancelled in current status");
        }
        
        if (rental.getStartTime().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Rental cannot be cancelled within 2 hours of start time");
        }
        
        // Update rental status
        rental.setStatus(RentalStatus.CANCELLED);
        
        // Update vehicle status
        Vehicle vehicle = rental.getVehicle();
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setAvailable(true);
        vehicleRepository.save(vehicle);
        
        Rental updatedRental = rentalRepository.save(rental);
        
        return rentalMapper.toResponseDto(updatedRental);
    }
    
    public List<RentalResponseDto> getCustomerRentals(Long customerId) {
        List<Rental> rentals = rentalRepository.findByCustomerId(customerId);
        return rentals.stream()
                .map(rentalMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public RentalResponseDto getRentalById(Long rentalId, Long customerId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        
        // Check authorization
        if (!rental.getCustomer().getId().equals(customerId)) {
            throw new BadRequestException("You are not authorized to view this rental");
        }
        
        return rentalMapper.toResponseDto(rental);
    }
}