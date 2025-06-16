package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.FlightAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FlightAuditLogRepository extends JpaRepository<FlightAuditLog, Integer> {
    List<FlightAuditLog> findByFlightIDOrderByChangeTimestampDesc(Integer flightID);

}