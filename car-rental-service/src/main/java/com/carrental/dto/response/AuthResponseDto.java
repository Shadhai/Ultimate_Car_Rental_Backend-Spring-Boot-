package com.carrental.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String refreshToken;
    private Long userId;
    private String name;
    private String email;
    private String role;
    private String message;
    private LocalDateTime expiresAt;
}