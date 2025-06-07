package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.CheckIns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIns, Integer> {

    @Procedure(procedureName = "sp_PerformCheckIn")
    Integer performCheckIn(@Param("TicketID") Integer ticketID);

    boolean existsByTicket_TicketID(Integer ticketID);

    Optional<CheckIns> findByTicket_TicketID(Integer ticketID);
}