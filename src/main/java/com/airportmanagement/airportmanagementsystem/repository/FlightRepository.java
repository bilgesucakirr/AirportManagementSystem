package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
}