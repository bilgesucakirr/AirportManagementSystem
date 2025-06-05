package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftRepository extends JpaRepository<Aircraft, Integer> {
}