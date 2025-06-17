package com.airportmanagement.airportmanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirportManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirportManagementSystemApplication.class, args);
    }

}
