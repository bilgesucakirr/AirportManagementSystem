package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.Ticket;
import com.airportmanagement.airportmanagementsystem.dto.BookingDetailsDTO;
import com.airportmanagement.airportmanagementsystem.dto.PassengerBookingDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    @Query(value = "EXEC sp_GetAllBookingsDetails", nativeQuery = true)
    List<BookingDetailsDTO> getAllBookingsDetails();

    @Procedure(procedureName = "sp_BookTicket")
    Integer bookTicket(@Param("PassengerID") Integer passengerID,
                       @Param("FlightID") Integer flightID,
                       @Param("SeatNumber") String seatNumber,
                       @Param("Price") BigDecimal price,
                       @Param("LuggageWeight") BigDecimal luggageWeight,
                       @Param("IsExtraLuggage") Boolean isExtraLuggage);

    @Query(value = "EXEC sp_GetPassengerBookings @UserID = :userID", nativeQuery = true)
    List<PassengerBookingDTO> getPassengerBookings(@Param("userID") Integer userID);

    @Procedure(procedureName = "sp_CancelTicket")
    Integer cancelTicket(@Param("TicketID") Integer ticketID);
}