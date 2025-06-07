package com.airportmanagement.airportmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithRoleDTO {
    private Integer userID;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Timestamp registrationDate;
    private String roles;
}