package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.Ticket;
import com.airportmanagement.airportmanagementsystem.dto.BookingDetailsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    @Query(value = "EXEC sp_GetAllBookingsDetails", nativeQuery = true)
    List<BookingDetailsDTO> getAllBookingsDetails();
}