package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.Route;
import com.airportmanagement.airportmanagementsystem.dto.RouteDetailsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Integer> {

    Optional<Route> findById(Integer id);

    @Procedure(procedureName = "sp_AddRoute")
    Integer addRoute(@Param("DepartureAirportID") Integer departureAirportID,
                     @Param("ArrivalAirportID") Integer arrivalAirportID,
                     @Param("EstimatedDurationMinutes") Integer estimatedDurationMinutes,
                     @Param("DistanceKm") Integer distanceKm);

    @Procedure(procedureName = "sp_UpdateRoute")
    Integer updateRoute(@Param("RouteID") Integer routeID,
                        @Param("DepartureAirportID") Integer departureAirportID,
                        @Param("ArrivalAirportID") Integer arrivalAirportID,
                        @Param("EstimatedDurationMinutes") Integer estimatedDurationMinutes,
                        @Param("DistanceKm") Integer distanceKm);

    @Procedure(procedureName = "sp_DeleteRoute")
    Integer deleteRoute(@Param("RouteID") Integer routeID);

    @Query(value = "EXEC sp_GetAllRoutes", nativeQuery = true)
    List<RouteDetailsDTO> findAllRoutesWithDetails();
}