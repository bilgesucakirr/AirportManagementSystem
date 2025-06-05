package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Integer> {
}