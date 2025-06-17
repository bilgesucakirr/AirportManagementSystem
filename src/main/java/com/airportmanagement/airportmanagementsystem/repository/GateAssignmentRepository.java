package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.GateAssignment;
import com.airportmanagement.airportmanagementsystem.entity.Gate;
import com.airportmanagement.airportmanagementsystem.dto.GateAssignmentDetailsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GateAssignmentRepository extends JpaRepository<GateAssignment, Integer> {

    @Procedure(procedureName = "sp_AssignGateToFlight", outputParameterName = "NewAssignmentID")
    Integer assignGateToFlight(@Param("FlightID") Integer flightID,
                               @Param("GateID") Integer gateID,
                               @Param("AssignmentStartTime") LocalDateTime assignmentStartTime,
                               @Param("AssignmentEndTime") LocalDateTime assignmentEndTime);

    @Procedure(procedureName = "sp_UpdateGateAssignment", outputParameterName = "ResultCode")
    Integer updateGateAssignment(@Param("AssignmentID") Integer assignmentID,
                                 @Param("NewFlightID") Integer newFlightID,
                                 @Param("NewGateID") Integer newGateID,
                                 @Param("NewAssignmentStartTime") LocalDateTime newAssignmentStartTime,
                                 @Param("NewAssignmentEndTime") LocalDateTime newAssignmentEndTime);

    @Procedure(procedureName = "sp_RemoveGateAssignment", outputParameterName = "ResultCode")
    Integer removeGateAssignment(@Param("AssignmentID") Integer assignmentID);

    @Query(name = "GateAssignment.getFlightGateDetails", nativeQuery = true)
    List<GateAssignmentDetailsDTO> getFlightGateDetails(@Param("flightID") Integer flightID);

    @Query(value = "EXEC sp_GetAvailableGatesForPeriod @AirportID = :airportID, @DesiredStartTime = :desiredStartTime, @DesiredEndTime = :desiredEndTime", nativeQuery = true)
    List<Gate> getAvailableGatesForPeriod(@Param("airportID") Integer airportID,
                                          @Param("desiredStartTime") LocalDateTime desiredStartTime,
                                          @Param("desiredEndTime") LocalDateTime desiredEndTime);

    @Query(name = "GateAssignment.getAllGateAssignmentsWithDetails", nativeQuery = true)
    List<GateAssignmentDetailsDTO> getAllGateAssignmentsWithDetails();
}