package com.carrental.service.customer;

import com.carrental.dto.request.LoginRequestDto;
import com.carrental.dto.request.SignupRequestDto;
import com.carrental.dto.response.AuthResponseDto;
import com.carrental.entity.User;
import com.carrental.enums.Role;
import com.carrental.enums.UserStatus;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.repository.UserRepository;
import com.carrental.service.BaseService;
import com.carrental.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerAuthService extends BaseService<User, Long> {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    @Override
    protected UserRepository getRepository() {
        return userRepository;
    }
    
    @Override
    protected String getEntityName() {
        return "User";
    }
    
    @Transactional
    public AuthResponseDto register(SignupRequestDto request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number already registered");
        }
        
        // Create new user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .driverLicense(request.getDriverLicense())
                .role(Role.CUSTOMER)
                .status(UserStatus.PENDING_VERIFICATION)
                .active(true)
                .build();
        
        userRepository.save(user);
        
        // Generate tokens
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        
        return AuthResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Registration successful. Please verify your email.")
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
    }
    
    public AuthResponseDto login(LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            // Check if user is active
            if (!user.isActive()) {
                throw new BadRequestException("Account is deactivated");
            }
            
            // Check if user is verified
            if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
                throw new BadRequestException("Please verify your email before logging in");
            }
            
            // Generate tokens
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
            
            return AuthResponseDto.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .message("Login successful")
                    .expiresAt(LocalDateTime.now().plusHours(24))
                    .build();
                    
        } catch (Exception e) {
            throw new BadRequestException("Invalid email or password");
        }
    }
    
    @Transactional
    public void verifyEmail(String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (user.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw new BadRequestException("Email already verified");
        }
        
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }
    
    public AuthResponseDto refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }
        
        String email = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String newToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        
        return AuthResponseDto.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Token refreshed successfully")
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
    }
}