package com.airportmanagement.airportmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatAvailabilityDTO {
    private Integer seatID;
    private String seatNumber;
    private String seatClass;
    private Boolean isAvailable;
}