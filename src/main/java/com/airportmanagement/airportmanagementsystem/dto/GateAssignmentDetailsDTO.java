package com.airportmanagement.airportmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GateAssignmentDetailsDTO {
    private Integer assignmentID;
    private Integer flightID;
    private String flightStatus;
    private Timestamp departureTime;
    private Timestamp arrivalTime;
    private String departureAirportCode;
    private String arrivalAirportCode;

    private Integer gateID;
    private String gateCode;
    private String gateAirportName;
    private String gateAirportCode;

    private Timestamp assignmentTime;
    private Timestamp startTime;
    private Timestamp endTime;


    public LocalDateTime getDepartureLocalDateTime() {
        return (departureTime != null) ? departureTime.toLocalDateTime() : null;
    }
    public LocalDateTime getArrivalLocalDateTime() {
        return (arrivalTime != null) ? arrivalTime.toLocalDateTime() : null;
    }
    public LocalDateTime getAssignmentLocalDateTime() {
        return (assignmentTime != null) ? assignmentTime.toLocalDateTime() : null;
    }
    public LocalDateTime getStartLocalDateTime() {
        return (startTime != null) ? startTime.toLocalDateTime() : null;
    }
    public LocalDateTime getEndLocalDateTime() {
        return (endTime != null) ? endTime.toLocalDateTime() : null;
    }
}