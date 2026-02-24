package com.carrental.repository;

import com.carrental.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByCity(String city);
    List<Location> findByActive(boolean active);
}