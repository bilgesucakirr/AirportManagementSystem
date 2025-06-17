package com.airportmanagement.airportmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.sql.Timestamp;

@NamedNativeQueries({
        // sp_GetFlightGateDetails için mapping
        @NamedNativeQuery(
                name = "GateAssignment.getFlightGateDetails",
                query = "EXEC sp_GetFlightGateDetails @FlightID = ?",
                resultSetMapping = "GateAssignmentDetailsDTOMapping"
        ),
        // sp_GetAllGateAssignmentsWithDetails için mapping
        @NamedNativeQuery(
                name = "GateAssignment.getAllGateAssignmentsWithDetails",
                query = "EXEC sp_GetAllGateAssignmentsWithDetails",
                resultSetMapping = "GateAssignmentDetailsDTOMapping"
        )
})
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "GateAssignmentDetailsDTOMapping",
                classes = @ConstructorResult(
                        targetClass = com.airportmanagement.airportmanagementsystem.dto.GateAssignmentDetailsDTO.class, // Hedef DTO sınıfı
                        columns = {
                                @ColumnResult(name = "AssignmentID", type = Integer.class),
                                @ColumnResult(name = "FlightID", type = Integer.class),
                                @ColumnResult(name = "FlightStatus", type = String.class), // SP'den geliyor
                                @ColumnResult(name = "DepartureTime", type = Timestamp.class), // SP'den geliyor
                                @ColumnResult(name = "ArrivalTime", type = Timestamp.class), // SP'den geliyor
                                @ColumnResult(name = "DepartureAirportCode", type = String.class), // SP'den geliyor
                                @ColumnResult(name = "ArrivalAirportCode", type = String.class), // SP'den geliyor
                                @ColumnResult(name = "GateID", type = Integer.class),
                                @ColumnResult(name = "GateCode", type = String.class), // SP'den geliyor
                                @ColumnResult(name = "GateAirportName", type = String.class), // SP'den geliyor
                                @ColumnResult(name = "GateAirportCode", type = String.class), // SP'den geliyor
                                @ColumnResult(name = "AssignmentTime", type = Timestamp.class),
                                @ColumnResult(name = "StartTime", type = Timestamp.class),
                                @ColumnResult(name = "EndTime", type = Timestamp.class)
                        }
                )
        )
})

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "GateAssignments")
public class GateAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer assignmentID;

    @ManyToOne
    @JoinColumn(name = "flightID")
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "gateID")
    private Gate gate;

    private LocalDateTime assignmentTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}