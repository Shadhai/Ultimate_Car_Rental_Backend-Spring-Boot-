package com.carrental.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class RevenueReportDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private BigDecimal platformCommission;
    private BigDecimal driverPayouts;
    private BigDecimal netProfit;
    private Integer totalBookings;
    private Integer completedBookings;
    private Map<String, BigDecimal> revenueByVehicleType;
    private Map<String, Integer> bookingsByLocation;
}