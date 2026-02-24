package com.carrental.service.admin;

import com.carrental.dto.response.AdminDashboardStatsDto;
import com.carrental.dto.response.RevenueReportDto;
import com.carrental.entity.DriverPayout;
import com.carrental.entity.Payment;
import com.carrental.entity.Rental;
import com.carrental.entity.Vehicle;
import com.carrental.enums.PaymentStatus;
import com.carrental.enums.PayoutStatus;
import com.carrental.enums.RentalStatus;
import com.carrental.enums.VehicleType;
import com.carrental.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminReportService {
    
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final DriverPayoutRepository driverPayoutRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final OfferRepository offerRepository;
    
    public AdminDashboardStatsDto getDashboardStats() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
                .atTime(LocalTime.MAX);
        
        // Calculate monthly revenue
        BigDecimal monthlyRevenue = BigDecimal.ZERO;
        List<Payment> monthlyPayments = paymentRepository.findByPaymentDateBetween(startOfMonth, endOfMonth);
        for (Payment payment : monthlyPayments) {
            if (payment.getStatus() == PaymentStatus.SUCCESS && payment.getAmount() != null) {
                monthlyRevenue = monthlyRevenue.add(payment.getAmount());
            }
        }
        
        // Calculate pending payouts
        BigDecimal pendingPayouts = BigDecimal.ZERO;
        List<DriverPayout> pendingPayoutList = driverPayoutRepository.findByStatus(PayoutStatus.PENDING);
        for (DriverPayout payout : pendingPayoutList) {
            if (payout.getDriverAmount() != null) {
                pendingPayouts = pendingPayouts.add(payout.getDriverAmount());
            }
        }
        
        // Calculate total payouts
        BigDecimal totalPayouts = BigDecimal.ZERO;
        List<DriverPayout> paidPayoutList = driverPayoutRepository.findByStatus(PayoutStatus.PAID);
        for (DriverPayout payout : paidPayoutList) {
            if (payout.getDriverAmount() != null) {
                totalPayouts = totalPayouts.add(payout.getDriverAmount());
            }
        }
        
        return AdminDashboardStatsDto.builder()
                .totalUsers(userRepository.count())
                .totalVehicles(vehicleRepository.count())
                .totalRentals(rentalRepository.count())
                .totalRevenue(calculateTotalRevenue())
                .monthlyRevenue(monthlyRevenue)
                .pendingPayouts(pendingPayouts)
                .totalPayouts(totalPayouts)
                .activeRentals(rentalRepository.countByStatus(RentalStatus.ONGOING))
                .build();
    }
    
    public RevenueReportDto getRevenueReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Get payments and rentals in date range
        List<Payment> payments = paymentRepository.findByPaymentDateBetween(startDateTime, endDateTime);
        List<Rental> rentals = rentalRepository.findRentalsBetweenDates(startDateTime, endDateTime);
        
        // Calculate revenue metrics
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal platformCommission = BigDecimal.ZERO;
        BigDecimal driverPayouts = BigDecimal.ZERO;
        int totalBookings = 0;
        int completedBookings = 0;
        
        for (Payment payment : payments) {
            if (payment.getStatus() == PaymentStatus.SUCCESS && payment.getAmount() != null) {
                totalRevenue = totalRevenue.add(payment.getAmount());
            }
        }
        
        // Calculate bookings
        totalBookings = rentals.size();
        for (Rental rental : rentals) {
            if (rental.getStatus() == RentalStatus.COMPLETED) {
                completedBookings++;
                // Calculate platform commission (assuming 20% commission)
                if (rental.getTotalFare() != null) {
                    BigDecimal commission = rental.getTotalFare().multiply(new BigDecimal("0.20"));
                    platformCommission = platformCommission.add(commission);
                    
                    // Calculate driver payout (80% of fare)
                    BigDecimal payout = rental.getTotalFare().multiply(new BigDecimal("0.80"));
                    driverPayouts = driverPayouts.add(payout);
                }
            }
        }
        
        // Calculate revenue by vehicle type
        Map<String, BigDecimal> revenueByVehicleType = calculateRevenueByVehicleType(rentals);
        
        // Calculate bookings by location
        Map<String, Integer> bookingsByLocation = calculateBookingsByLocation(rentals);
        
        // Calculate net profit
        BigDecimal netProfit = totalRevenue.subtract(driverPayouts);
        
        // Create and populate DTO
        RevenueReportDto report = new RevenueReportDto();
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setTotalRevenue(totalRevenue);
        report.setPlatformCommission(platformCommission);
        report.setDriverPayouts(driverPayouts);
        report.setNetProfit(netProfit);
        report.setTotalBookings(totalBookings);
        report.setCompletedBookings(completedBookings);
        report.setRevenueByVehicleType(revenueByVehicleType);
        report.setBookingsByLocation(bookingsByLocation);
        
        return report;
    }
    
    private Map<String, BigDecimal> calculateRevenueByVehicleType(List<Rental> rentals) {
        Map<String, BigDecimal> revenueByType = new HashMap<>();
        
        for (Rental rental : rentals) {
            if (rental.getStatus() == RentalStatus.COMPLETED && rental.getTotalFare() != null) {
                if (rental.getVehicle() != null && rental.getVehicle().getType() != null) {
                    String vehicleType = rental.getVehicle().getType().name();
                    BigDecimal currentRevenue = revenueByType.getOrDefault(vehicleType, BigDecimal.ZERO);
                    revenueByType.put(vehicleType, currentRevenue.add(rental.getTotalFare()));
                }
            }
        }
        
        return revenueByType;
    }
    
    private Map<String, Integer> calculateBookingsByLocation(List<Rental> rentals) {
        Map<String, Integer> bookingsByLocation = new HashMap<>();
        
        for (Rental rental : rentals) {
            if (rental.getVehicle() != null && rental.getVehicle().getLocation() != null) {
                String locationName = rental.getVehicle().getLocation().getName();
                int currentCount = bookingsByLocation.getOrDefault(locationName, 0);
                bookingsByLocation.put(locationName, currentCount + 1);
            }
        }
        
        return bookingsByLocation;
    }
    
    private BigDecimal calculateTotalRevenue() {
        BigDecimal total = BigDecimal.ZERO;
        List<Payment> payments = paymentRepository.findAll();
        for (Payment payment : payments) {
            if (payment.getStatus() == PaymentStatus.SUCCESS && payment.getAmount() != null) {
                total = total.add(payment.getAmount());
            }
        }
        return total;
    }
}