package com.airportmanagement.airportmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "CheckIns")
public class CheckIns {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer checkInID;

    @ManyToOne
    @JoinColumn(name = "ticketID")
    private Ticket ticket;
    private LocalDateTime checkInTime;

}