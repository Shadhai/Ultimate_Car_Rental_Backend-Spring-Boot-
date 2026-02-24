package com.carrental.dto.response;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String role;
    private String status;
    private String driverLicense;
    private String profileImageUrl;
    private Long locationId;
    private String locationName;
    private String createdAt;
    private boolean active;
}