package com.carrental.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DriverPayoutResponseDto {
    private Long id;
    private Long driverId;
    private String driverName;
    private Long rentalId;
    private BigDecimal totalFare;
    private BigDecimal platformCommission;
    private BigDecimal driverAmount;
    private String status;
    private String payoutDate; 
    private String transactionReference;
    private String paymentMethod;
    private String createdAt; 
}