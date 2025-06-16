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
@Table(name = "FlightAuditLog")
public class FlightAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer auditID;

    private Integer flightID;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime oldDepartureTime;
    private LocalDateTime newDepartureTime;
    private LocalDateTime oldArrivalTime;
    private LocalDateTime newArrivalTime;
    private LocalDateTime changeTimestamp;
    private String changedBy;
}