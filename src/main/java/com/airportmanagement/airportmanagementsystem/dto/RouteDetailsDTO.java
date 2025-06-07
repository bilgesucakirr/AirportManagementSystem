package com.airportmanagement.airportmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDetailsDTO {
    private Integer routeID;
    private Integer departureAirportID;
    private String departureAirportName;
    private String departureAirportCode;
    private Integer arrivalAirportID;
    private String arrivalAirportName;
    private String arrivalAirportCode;
    private Integer estimatedDurationMinutes;
    private Integer distanceKm;
}