package com.carrental.entity;

import com.carrental.entity.base.BaseEntity;
import com.carrental.enums.DamageSeverity;
import com.carrental.enums.DamageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "damage_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DamageReport extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    @ManyToOne
    @JoinColumn(name = "reported_by")
    private User reportedBy;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private DamageSeverity severity;

    @Enumerated(EnumType.STRING)
    private DamageStatus status;

    private BigDecimal repairCost;

    @Column(name = "estimated_repair_days")
    private Integer estimatedRepairDays;

    @Column(name = "repair_shop")
    private String repairShop;

    @Column(name = "repair_completion_date")
    private java.time.LocalDate repairCompletionDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_insured")
    private boolean insured;
}