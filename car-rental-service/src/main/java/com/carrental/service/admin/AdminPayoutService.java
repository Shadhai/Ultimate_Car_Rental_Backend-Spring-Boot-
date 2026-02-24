package com.carrental.service.admin;

import com.carrental.dto.response.DriverPayoutResponseDto;
import com.carrental.entity.DriverPayout;
import com.carrental.entity.Rental;
import com.carrental.enums.PayoutStatus;
import com.carrental.enums.RentalStatus;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.mapper.PayoutMapper;
import com.carrental.repository.DriverPayoutRepository;
import com.carrental.repository.RentalRepository;
import com.carrental.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPayoutService extends BaseService<DriverPayout, Long> {
    
    private final DriverPayoutRepository payoutRepository;
    private final RentalRepository rentalRepository;
    private final PayoutMapper payoutMapper;
    
    @Override
    protected DriverPayoutRepository getRepository() {
        return payoutRepository;
    }
    
    @Override
    protected String getEntityName() {
        return "DriverPayout";
    }
    
    @Transactional
    public DriverPayoutResponseDto generatePayoutForRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        
        // Check if rental is completed
        if (rental.getStatus() != RentalStatus.COMPLETED) {
            throw new BadRequestException("Payout can only be generated for completed rentals");
        }
        
        // Check if payout already exists for this rental
        List<DriverPayout> existingPayouts = payoutRepository.findByDriverId(rental.getDriver().getId());
        boolean payoutExists = existingPayouts.stream()
                .anyMatch(p -> p.getRental().getId().equals(rentalId));
        
        if (payoutExists) {
            throw new BadRequestException("Payout already generated for this rental");
        }
        
        // Calculate payout (assuming 20% platform commission)
        BigDecimal platformCommissionPercentage = new BigDecimal("20.00");
        BigDecimal totalFare = rental.getTotalFare();
        BigDecimal platformCommission = totalFare.multiply(platformCommissionPercentage)
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal driverAmount = totalFare.subtract(platformCommission);
        
        // Create payout
        DriverPayout payout = DriverPayout.builder()
                .driver(rental.getDriver())
                .rental(rental)
                .totalFare(totalFare)
                .platformCommission(platformCommission)
                .driverAmount(driverAmount)
                .status(PayoutStatus.PENDING)
                .payoutDate(null)
                .build();
        
        DriverPayout savedPayout = payoutRepository.save(payout);
        
        // Update rental with commission and driver amount
        rental.setPlatformCommission(platformCommission);
        rental.setDriverAmount(driverAmount);
        rentalRepository.save(rental);
        
        return payoutMapper.toResponseDto(savedPayout);
    }
    
    @Transactional
    public DriverPayoutResponseDto processPayout(Long payoutId, String paymentMethod, String transactionReference) {
        DriverPayout payout = findById(payoutId);
        
        if (payout.getStatus() != PayoutStatus.PENDING) {
            throw new BadRequestException("Only pending payouts can be processed");
        }
        
        // Here you would integrate with payment gateway (PayPal, Stripe, bank transfer, etc.)
        // For now, simulate successful payment
        
        payout.setStatus(PayoutStatus.PAID);
        payout.setPayoutDate(LocalDateTime.now());
        payout.setPaymentMethod(paymentMethod);
        payout.setTransactionReference(transactionReference);
        
        DriverPayout updatedPayout = payoutRepository.save(payout);
        return payoutMapper.toResponseDto(updatedPayout);
    }
    
    @Transactional
    public DriverPayoutResponseDto markAsFailed(Long payoutId, String reason) {
        DriverPayout payout = findById(payoutId);
        
        if (payout.getStatus() != PayoutStatus.PENDING) {
            throw new BadRequestException("Only pending payouts can be marked as failed");
        }
        
        payout.setStatus(PayoutStatus.FAILED);
        payout.setTransactionReference("FAILED: " + reason);
        
        DriverPayout updatedPayout = payoutRepository.save(payout);
        return payoutMapper.toResponseDto(updatedPayout);
    }
    
    public List<DriverPayoutResponseDto> getAllPayouts() {
        return payoutRepository.findAll().stream()
                .map(payoutMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public Page<DriverPayoutResponseDto> getPayoutsByPage(Pageable pageable) {
        return payoutRepository.findAll(pageable)
                .map(payoutMapper::toResponseDto);
    }
    
    public List<DriverPayoutResponseDto> getPayoutsByStatus(PayoutStatus status) {
        return payoutRepository.findByStatus(status).stream()
                .map(payoutMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<DriverPayoutResponseDto> getPayoutsByDriver(Long driverId) {
        return payoutRepository.findByDriverId(driverId).stream()
                .map(payoutMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public BigDecimal getTotalPendingAmount() {
        BigDecimal amount = payoutRepository.getTotalPendingAmount();
        return amount != null ? amount : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalPaidAmount() {
        BigDecimal amount = payoutRepository.getTotalPaidAmount();
        return amount != null ? amount : BigDecimal.ZERO;
    }
    
    public List<DriverPayoutResponseDto> getPendingPayoutsByDriver(Long driverId) {
        return payoutRepository.findByDriverId(driverId).stream()
                .filter(p -> p.getStatus() == PayoutStatus.PENDING)
                .map(payoutMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}