package com.airportmanagement.airportmanagementsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ScheduledTasksService {

    @Autowired
    private JdbcTemplate jdbcTemplate; // JdbcTemplate'i enjekte et

    @Scheduled(fixedRate = 300000) // Her 5 dakikada bir (test için)

    @Transactional
    public void updateFlightStatuses() {
        System.out.println("Scheduled task: Updating expired flight statuses at " + LocalDateTime.now());
        try {

            jdbcTemplate.execute("EXEC sp_set_session_context 'is_automated_update', 1;");

            jdbcTemplate.execute("EXEC sp_UpdateExpiredFlightStatuses;");

            jdbcTemplate.execute("EXEC sp_set_session_context 'is_automated_update', NULL;");

            System.out.println("Scheduled task: Flight statuses updated successfully.");
        } catch (Exception e) {
            System.err.println("Scheduled task: Error updating flight statuses: " + e.getMessage());
            e.printStackTrace();
        }
    }
}