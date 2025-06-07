package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.Luggage;
import com.airportmanagement.airportmanagementsystem.dto.LuggageDetailsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface LuggageRepository extends JpaRepository<Luggage, Integer> {

    @Procedure(procedureName = "sp_AddLuggage")
    Integer addLuggage(@Param("TicketID") Integer ticketID,
                       @Param("Weight") BigDecimal weight,
                       @Param("IsExtra") Boolean isExtra);

    @Procedure(procedureName = "sp_UpdateLuggage")
    Integer updateLuggage(@Param("LuggageID") Integer luggageID,
                          @Param("Weight") BigDecimal weight,
                          @Param("IsExtra") Boolean isExtra);

    @Procedure(procedureName = "sp_DeleteLuggage")
    Integer deleteLuggage(@Param("LuggageID") Integer luggageID);


    @Query(name = "Luggage.getLuggageByTicketID", nativeQuery = true)
    List<LuggageDetailsDTO> getLuggageByTicketID(@Param("TicketID") Integer ticketID);

    @Query(value = "EXEC sp_GetTotalLuggageItems", nativeQuery = true)
    Integer countTotalLuggageItems();

    @Query(value = "EXEC sp_GetTotalExtraLuggageItems", nativeQuery = true)
    Integer countTotalExtraLuggageItems();

    @Query(value = "EXEC sp_GetTotalLuggageWeight", nativeQuery = true)
    BigDecimal sumTotalLuggageWeight();

    @Query(name = "Luggage.getAllLuggageDetails", nativeQuery = true)
    List<LuggageDetailsDTO> getAllLuggageDetails();
}