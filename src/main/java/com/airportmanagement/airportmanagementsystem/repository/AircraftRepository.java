package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AircraftRepository extends JpaRepository<Aircraft, Integer> {

    @Procedure(procedureName = "sp_AddAircraft")
    Integer addAircraft(@Param("Model") String model,
                        @Param("Capacity") Integer capacity,
                        @Param("TailNumber") String tailNumber,
                        @Param("RangeMiles") Integer rangeMiles); // NewAircraftID kaldırıldı, metot dönüş değeri olarak gelecek

    @Procedure(procedureName = "sp_UpdateAircraft")
    Integer updateAircraft(@Param("AircraftID") Integer aircraftID,
                           @Param("Model") String model,
                           @Param("Capacity") Integer capacity,
                           @Param("TailNumber") String tailNumber,
                           @Param("RangeMiles") Integer rangeMiles); // ResultCode kaldırıldı, metot dönüş değeri olarak gelecek

    @Procedure(procedureName = "sp_DeleteAircraft")
    Integer deleteAircraft(@Param("AircraftID") Integer aircraftID); // ResultCode kaldırıldı, metot dönüş değeri olarak gelecek

    @Query(value = "EXEC sp_GetAllAircrafts", nativeQuery = true)
    List<Aircraft> getAllAircrafts();
}