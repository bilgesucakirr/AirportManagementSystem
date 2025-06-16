package com.airportmanagement.airportmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Flights")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer flightID;

    @ManyToOne
    @JoinColumn(name = "routeID")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "aircraftID")
    private Aircraft aircraft;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String status;

    private Integer bookedSeats;
}