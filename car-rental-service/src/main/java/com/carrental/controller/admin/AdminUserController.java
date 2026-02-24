package com.carrental.controller.admin;

import com.carrental.dto.request.UserUpdateRequestDto;
import com.carrental.dto.response.ApiResponse;
import com.carrental.dto.response.UserResponseDto;
import com.carrental.enums.Role;
import com.carrental.enums.UserStatus;
import com.carrental.service.admin.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    
    private final AdminUserService userService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> response = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        UserResponseDto response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User details retrieved", response));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDto request) {
        UserResponseDto response = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }
    
    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        UserResponseDto response = userService.updateUserRole(id, role);
        return ResponseEntity.ok(ApiResponse.success("User role updated", response));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam UserStatus status) {
        UserResponseDto response = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("User status updated", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", null));
    }
}