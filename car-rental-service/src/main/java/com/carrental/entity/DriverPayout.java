package com.carrental.entity;

import com.carrental.entity.base.BaseEntity;
import com.carrental.enums.PayoutStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "driver_payout")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverPayout extends BaseEntity { // EXTEND BaseEntity
    // Remove @Id and @GeneratedValue since BaseEntity already has them
    
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    @ManyToOne
    @JoinColumn(name = "rental_id")
    private Rental rental;

    private BigDecimal totalFare;
    private BigDecimal platformCommission;
    private BigDecimal driverAmount;

    @Enumerated(EnumType.STRING)
    private PayoutStatus status;

    private LocalDateTime payoutDate;
    private String transactionReference;
    private String paymentMethod;
}