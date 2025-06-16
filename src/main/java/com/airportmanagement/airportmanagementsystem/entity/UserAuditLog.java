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
@Table(name = "UserAuditLog")
public class UserAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer auditID;

    private Integer userID;
    private String actionType;
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String oldValues;
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String newValues;
    private LocalDateTime changeTimestamp;
    private String changedBy;
}