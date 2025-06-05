package com.airportmanagement.airportmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Passengers")
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer passengerID;

    @OneToOne
    @JoinColumn(name = "userID")
    private User user;

    private String nationality;
    private String passportNumber;
}