package com.carrental.service.driver;

import com.carrental.dto.response.DriverEarningsDto;
import com.carrental.dto.response.DriverPayoutResponseDto;
import com.carrental.entity.DriverPayout;
import com.carrental.entity.User;
import com.carrental.enums.PayoutStatus;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.mapper.PayoutMapper;
import com.carrental.repository.DriverPayoutRepository;
import com.carrental.repository.UserRepository;
import com.carrental.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverPayoutService extends BaseService<DriverPayout, Long> {
    
    private final DriverPayoutRepository payoutRepository;
    private final UserRepository userRepository;
    private final PayoutMapper payoutMapper;
    
    @Override
    protected DriverPayoutRepository getRepository() {
        return payoutRepository;
    }
    
    @Override
    protected String getEntityName() {
        return "DriverPayout";
    }
    
    public List<DriverPayoutResponseDto> getDriverPayouts(Long driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        List<DriverPayout> payouts = payoutRepository.findByDriverId(driverId);
        return payouts.stream()
                .map(payoutMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<DriverPayoutResponseDto> getPayoutsByStatus(Long driverId, PayoutStatus status) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        return payoutRepository.findByDriverId(driverId).stream()
                .filter(p -> p.getStatus() == status)
                .map(payoutMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public DriverPayoutResponseDto getPayoutDetails(Long payoutId, Long driverId) {
        DriverPayout payout = findById(payoutId);
        
        // Authorization check
        if (!payout.getDriver().getId().equals(driverId)) {
            throw new ResourceNotFoundException("Payout not found");
        }
        
        return payoutMapper.toResponseDto(payout);
    }
    
    public DriverEarningsDto getEarningsSummary(Long driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        List<DriverPayout> allPayouts = payoutRepository.findByDriverId(driverId);
        
        BigDecimal totalEarnings = allPayouts.stream()
                .map(DriverPayout::getDriverAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal pendingEarnings = allPayouts.stream()
                .filter(p -> p.getStatus() == PayoutStatus.PENDING)
                .map(DriverPayout::getDriverAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal paidEarnings = allPayouts.stream()
                .filter(p -> p.getStatus() == PayoutStatus.PAID)
                .map(DriverPayout::getDriverAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Monthly earnings
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(
                LocalDateTime.now().toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
        
        BigDecimal monthlyEarnings = payoutRepository.findByDriverIdAndDateRange(driverId, startOfMonth, endOfMonth)
                .stream()
                .filter(p -> p.getStatus() == PayoutStatus.PAID)
                .map(DriverPayout::getDriverAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        DriverEarningsDto earnings = new DriverEarningsDto();
        // Set fields based on your DriverEarningsDto structure
        
        return earnings;
    }
    
    public List<DriverPayoutResponseDto> getPayoutHistory(Long driverId, LocalDateTime startDate, LocalDateTime endDate) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        return payoutRepository.findByDriverIdAndDateRange(driverId, startDate, endDate)
                .stream()
                .map(payoutMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public BigDecimal getTotalEarnings(Long driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        List<DriverPayout> payouts = payoutRepository.findByDriverId(driverId);
        return payouts.stream()
                .map(DriverPayout::getDriverAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getAvailableBalance(Long driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        return payoutRepository.findByDriverId(driverId).stream()
                .filter(p -> p.getStatus() == PayoutStatus.PENDING)
                .map(DriverPayout::getDriverAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}