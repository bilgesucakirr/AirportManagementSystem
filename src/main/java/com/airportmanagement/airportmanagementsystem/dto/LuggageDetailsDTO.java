package com.airportmanagement.airportmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LuggageDetailsDTO {
    private Integer luggageID;
    private Integer ticketID;
    private Integer checkInID;
    private BigDecimal weight;
    private Boolean isExtra;
    private BigDecimal calculatedFee;
    private String luggageType;


    private Integer checkInTicketID;
    private String passengerFullName;
    private String passengerEmail;
    private Integer flightID;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private Timestamp departureTime;
    private Timestamp arrivalTime;
    private String aircraftModel;
    private String aircraftTailNumber;

    public LocalDateTime getDepartureLocalDateTime() {
        return (departureTime != null) ? departureTime.toLocalDateTime() : null;
    }

    public LocalDateTime getArrivalLocalDateTime() {
        return (arrivalTime != null) ? arrivalTime.toLocalDateTime() : null;
    }
}