package com.carrental.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DriverEarningsDto {
    private Long rentalId;
    private String customerName;
    private String vehicleDetails;
    private LocalDateTime rentalDate;
    private BigDecimal totalFare;
    private BigDecimal platformCommission;
    private BigDecimal driverAmount;
    private String status;
    private LocalDateTime payoutDate;
}