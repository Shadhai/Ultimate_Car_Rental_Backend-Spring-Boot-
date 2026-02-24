package com.carrental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStatsDto {
    private Long totalUsers;
    private Long totalCustomers;
    private Long totalDrivers;
    private Long totalVehicles;
    private Long availableVehicles;
    private Long totalRentals;
    private Long activeRentals;
    private Long completedRentals;
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal pendingPayouts;
    private BigDecimal totalPayouts;
    private Long pendingMaintenance;
    private Long activeOffers;
}