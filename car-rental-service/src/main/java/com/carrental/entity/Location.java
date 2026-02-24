package com.carrental.entity;

import com.carrental.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String city;
    private String state;
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    private Double latitude;
    private Double longitude;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(name = "is_active")
    private boolean active = true;
}