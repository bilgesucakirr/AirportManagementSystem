package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.dto.SeatAvailabilityDTO;
import com.airportmanagement.airportmanagementsystem.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Integer> {

    @Query(value = "EXEC sp_GetFlightAvailableSeats @FlightID = :flightID", nativeQuery = true)
    List<SeatAvailabilityDTO> getFlightAvailableSeats(@Param("flightID") Integer flightID);


    List<Seat> findByAircraft_AircraftID(Integer aircraftID);

    Optional<Seat> findByAircraft_AircraftIDAndSeatNumber(Integer aircraftID, String seatNumber);

}