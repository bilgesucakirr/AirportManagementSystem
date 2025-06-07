package com.airportmanagement.airportmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer routeID;

    @ManyToOne
    @JoinColumn(name = "departureAirportID")
    private Airport departureAirport;

    @ManyToOne
    @JoinColumn(name = "arrivalAirportID")
    private Airport arrivalAirport;

    private Integer estimatedDurationMinutes;
    private Integer distanceKm;
}