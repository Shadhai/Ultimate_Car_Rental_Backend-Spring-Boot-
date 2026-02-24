package com.carrental.service.admin;

import com.carrental.dto.request.UserUpdateRequestDto;
import com.carrental.dto.response.UserResponseDto;
import com.carrental.entity.User;
import com.carrental.enums.Role;
import com.carrental.enums.UserStatus;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.mapper.UserMapper;
import com.carrental.repository.UserRepository;
import com.carrental.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService extends BaseService<User, Long> {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    protected UserRepository getRepository() {
        return userRepository;
    }
    
    @Override
    protected String getEntityName() {
        return "User";
    }
    
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public Page<UserResponseDto> getUsersByPage(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponseDto);
    }
    
    public List<UserResponseDto> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<UserResponseDto> getUsersByStatus(UserStatus status) {
        return userRepository.findByStatus(status).stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    public UserResponseDto getUserById(Long id) {
        User user = findById(id);
        return userMapper.toResponseDto(user);
    }
    
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateRequestDto request) {
        User user = findById(id);
        
        // Check if email is being changed and if it's already taken
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already taken");
            }
            user.setEmail(request.getEmail());
        }
        
        // Check if phone is being changed and if it's already taken
        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new BadRequestException("Phone number already taken");
            }
            user.setPhone(request.getPhone());
        }
        
        // Update other fields
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        
        if (request.getDriverLicense() != null) {
            user.setDriverLicense(request.getDriverLicense());
        }
        
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDto(updatedUser);
    }
    
    @Transactional
    public UserResponseDto updateUserRole(Long id, Role role) {
        User user = findById(id);
        
        // Prevent changing own role
        // This check would be done at controller level with current user context
        
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        
        return userMapper.toResponseDto(updatedUser);
    }
    
    @Transactional
    public UserResponseDto updateUserStatus(Long id, UserStatus status) {
        User user = findById(id);
        user.setStatus(status);
        
        // Deactivate user if status is not ACTIVE
        if (status != UserStatus.ACTIVE) {
            user.setActive(false);
        } else {
            user.setActive(true);
        }
        
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDto(updatedUser);
    }
    
    @Transactional
    public void changePassword(Long id, String newPassword) {
        User user = findById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        
        // Soft delete - mark as deleted
        user.setStatus(UserStatus.DELETED);
        user.setActive(false);
        userRepository.save(user);
    }
    
    @Transactional
    public void permanentDeleteUser(Long id) {
        // Check if user has active rentals
        // This would require checking rental repository
        // For now, just hard delete
        userRepository.deleteById(id);
    }
    
    public Long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }
    
    public List<UserResponseDto> searchUsers(String keyword) {
        // This would require a custom query
        // For now, filter from all users (not efficient for large datasets)
        return userRepository.findAll().stream()
                .filter(user -> user.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                               user.getEmail().toLowerCase().contains(keyword.toLowerCase()) ||
                               (user.getPhone() != null && user.getPhone().contains(keyword)))
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}