package com.carrental.repository;

import com.carrental.entity.User;
import com.carrental.enums.Role;
import com.carrental.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    List<User> findByRole(Role role);
    List<User> findByStatus(UserStatus status);
    List<User> findByLocationId(Long locationId);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    
    @Query("SELECT u FROM User u WHERE u.role = 'DRIVER' AND u.status = 'ACTIVE'")
    List<User> findAvailableDrivers();
    
    // KEEP ONLY ONE countByRole method
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER' AND u.status = 'PENDING_VERIFICATION'")
    Long countPendingVerifications();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    Long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);
}