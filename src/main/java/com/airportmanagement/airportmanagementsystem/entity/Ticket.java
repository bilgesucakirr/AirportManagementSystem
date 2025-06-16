package com.airportmanagement.airportmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ticketID;

    @ManyToOne
    @JoinColumn(name = "passengerID")
    private Passenger passenger;

    @ManyToOne
    @JoinColumn(name = "flightID")
    private Flight flight;

    private String seatNumber;
    private BigDecimal price;
    private LocalDateTime purchaseDate;
    private String status;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Luggage> luggageItems;
}