package com.airportmanagement.airportmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ApplicationSettings")
public class ApplicationSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer settingID;

    private String backupDirectory;
    private String emailAlertsRecipient;
    private Boolean archiveDataEnabled;
    private Integer archiveRetentionDays;
    private Integer minimumFlightCapacity;
    private Integer minimumTurnaroundMinutes;

    private Integer maximumCheckInHoursBeforeDeparture;
    private Integer minimumCheckInMinutesBeforeDeparture;

    private Integer standardLuggageWeightKg;
    private BigDecimal extraLuggageFeePerKg;
}