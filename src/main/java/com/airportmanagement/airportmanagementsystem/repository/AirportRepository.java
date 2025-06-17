package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport, Integer> {

    Optional<Airport> findByCode(String code);

    @Procedure(procedureName = "sp_AddAirport")
    Integer addAirport(@Param("AirportName") String airportName,
                       @Param("Location") String location,
                       @Param("Code") String code,
                       @Param("AirportType") String airportType);

    @Procedure(procedureName = "sp_UpdateAirport")
    Integer updateAirport(@Param("AirportID") Integer airportID,
                          @Param("AirportName") String airportName,
                          @Param("Location") String location,
                          @Param("Code") String code,
                          @Param("AirportType") String airportType);

    @Procedure(procedureName = "sp_DeleteAirport")
    Integer deleteAirport(@Param("AirportID") Integer airportID);

    @Query(value = "EXEC sp_GetAllAirports", nativeQuery = true)
    List<Airport> getAllAirports();
}