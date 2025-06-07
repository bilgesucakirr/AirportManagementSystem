package com.airportmanagement.airportmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchDTO {
    private Integer flightID;
    private Timestamp departureTime;
    private Timestamp arrivalTime;
    private String status;
    private String departureAirportName;
    private String departureAirportCode;
    private String departureLocation;
    private String arrivalAirportName;
    private String arrivalAirportCode;
    private String arrivalLocation;
    private String aircraftModel;
    private Integer aircraftCapacity;
    private String aircraftTailNumber;
    private Integer availableSeats;

    public LocalDateTime getDepartureLocalDateTime() {
        return (departureTime != null) ? departureTime.toLocalDateTime() : null;
    }

    public LocalDateTime getArrivalLocalDateTime() {
        return (arrivalTime != null) ? arrivalTime.toLocalDateTime() : null;
    }
}