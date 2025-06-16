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
public class PassengerBookingDTO {
    private Integer ticketID;
    private String seatNumber;
    private BigDecimal price;
    private Timestamp purchaseDate;
    private Integer flightID;
    private Timestamp departureTime;
    private Timestamp arrivalTime;
    private String flightStatus;
    private String departureAirportCode;
    private String departureAirportName;
    private String arrivalAirportCode;
    private String arrivalAirportName;
    private String aircraftModel;
    private String aircraftTailNumber;
    private Integer passengerUserID;


    private boolean isCheckedIn;
    private String status;

    public LocalDateTime getPurchaseLocalDateTime() {
        return (purchaseDate != null) ? purchaseDate.toLocalDateTime() : null;
    }

    public LocalDateTime getDepartureLocalDateTime() {
        return (departureTime != null) ? departureTime.toLocalDateTime() : null;
    }

    public LocalDateTime getArrivalLocalDateTime() {
        return (arrivalTime != null) ? arrivalTime.toLocalDateTime() : null;
    }
}