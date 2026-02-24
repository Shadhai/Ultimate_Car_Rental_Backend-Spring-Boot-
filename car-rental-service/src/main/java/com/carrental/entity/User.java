package com.carrental.entity;

import com.carrental.entity.base.BaseEntity;
import com.carrental.enums.Role;
import com.carrental.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "is_active")
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "driver_license")
    private String driverLicense;

    @Column(name = "profile_image_url")
    private String profileImageUrl;
}