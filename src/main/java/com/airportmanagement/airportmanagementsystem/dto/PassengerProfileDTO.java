package com.airportmanagement.airportmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerProfileDTO {
    private Integer userID;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Timestamp registrationDate;
    private Integer passengerID;
    private String nationality;
    private String passportNumber;

    public LocalDateTime getRegistrationLocalDateTime() {
        return (registrationDate != null) ? registrationDate.toLocalDateTime() : null;
    }
}