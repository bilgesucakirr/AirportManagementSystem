package com.airportmanagement.airportmanagementsystem.dto;

import com.airportmanagement.airportmanagementsystem.entity.Aircraft;
import com.airportmanagement.airportmanagementsystem.entity.Airport;
import com.airportmanagement.airportmanagementsystem.entity.Route;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDetailsDTO {
    private Integer flightID;
    private Integer routeID;
    private Integer departureAirportID;
    private String departureAirportName;
    private String departureAirportCode;
    private Integer arrivalAirportID;
    private String arrivalAirportName;
    private String arrivalAirportCode;
    private Integer estimatedDurationMinutes;
    private Integer aircraftID;
    private String aircraftModel;
    private Integer aircraftCapacity;
    private String aircraftTailNumber;
    private Timestamp departureTime;
    private Timestamp arrivalTime;
    private String status;


    public LocalDateTime getDepartureLocalDateTime() {
        return (departureTime != null) ? departureTime.toLocalDateTime() : null;
    }

    public LocalDateTime getArrivalLocalDateTime() {
        return (arrivalTime != null) ? arrivalTime.toLocalDateTime() : null;
    }
}