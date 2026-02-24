package com.carrental.entity;

import com.carrental.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "insurances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insurance extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "provider_name", nullable = false)
    private String providerName;

    @Column(name = "policy_number", nullable = false, unique = true)
    private String policyNumber;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    private BigDecimal premium;

    @Column(name = "coverage_amount")
    private BigDecimal coverageAmount;

    @Column(name = "coverage_type")
    private String coverageType;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @Column(name = "agent_contact")
    private String agentContact;

    @Column(name = "is_active")
    private boolean active = true;
}