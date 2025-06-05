package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
}