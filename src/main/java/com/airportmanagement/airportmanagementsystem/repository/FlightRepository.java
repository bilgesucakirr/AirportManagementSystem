package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.dto.FlightDetailsDTO;
import com.airportmanagement.airportmanagementsystem.dto.FlightSearchDTO;
import com.airportmanagement.airportmanagementsystem.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
    long countByDepartureTimeAfter(LocalDateTime time);

    @Query(value = "EXEC sp_GetAllFlights", nativeQuery = true)
    List<FlightDetailsDTO> findAllFlightsWithDetails();

    @Procedure(procedureName = "sp_AddFlight")
    Integer addFlight(@Param("RouteID") Integer routeID,
                      @Param("AircraftID") Integer aircraftID,
                      @Param("DepartureTime") LocalDateTime departureTime,
                      @Param("ArrivalTime") LocalDateTime arrivalTime,
                      @Param("Status") String status);

    @Procedure(procedureName = "sp_UpdateFlight")
    Integer updateFlight(@Param("FlightID") Integer flightID,
                         @Param("RouteID") Integer routeID,
                         @Param("AircraftID") Integer aircraftID,
                         @Param("DepartureTime") LocalDateTime departureTime,
                         @Param("ArrivalTime") LocalDateTime arrivalTime,
                         @Param("Status") String status);

    @Procedure(procedureName = "sp_DeleteFlight")
    Integer deleteFlight(@Param("FlightID") Integer flightID);


    @Query(value = "EXEC sp_SearchAvailableFlights " +
            "@DepartureAirportCode = :departureAirportCode, " +
            "@ArrivalAirportCode = :arrivalAirportCode, " +
            "@DepartureDate = :departureDate, " +
            "@MinAvailableSeats = :minAvailableSeats",
            nativeQuery = true)
    List<FlightSearchDTO> searchAvailableFlights(@Param("departureAirportCode") String departureAirportCode,
                                                 @Param("arrivalAirportCode") String arrivalAirportCode,
                                                 @Param("departureDate") LocalDateTime departureDate,
                                                 @Param("minAvailableSeats") Integer minAvailableSeats);
}