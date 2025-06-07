package com.airportmanagement.airportmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.sql.Timestamp;

@NamedNativeQueries({
        @NamedNativeQuery(
                name = "Luggage.getAllLuggageDetails",
                query = "SELECT * FROM v_LuggageDetails",
                resultSetMapping = "LuggageDetailsDTOMapping"
        ),
        @NamedNativeQuery(
                name = "Luggage.getLuggageByTicketID",
                query = "EXEC sp_GetLuggageByTicketID @TicketID = ?",
                resultSetMapping = "LuggageDetailsDTOMapping"
        )
})
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "LuggageDetailsDTOMapping",
                classes = @ConstructorResult(
                        targetClass = com.airportmanagement.airportmanagementsystem.dto.LuggageDetailsDTO.class,
                        columns = {
                                @ColumnResult(name = "LuggageID", type = Integer.class),
                                @ColumnResult(name = "TicketID", type = Integer.class),
                                @ColumnResult(name = "CheckInID", type = Integer.class),
                                @ColumnResult(name = "Weight", type = BigDecimal.class),
                                @ColumnResult(name = "IsExtra", type = Boolean.class),
                                @ColumnResult(name = "CalculatedFee", type = BigDecimal.class),
                                @ColumnResult(name = "LuggageType", type = String.class),
                                @ColumnResult(name = "CheckInTicketID", type = Integer.class),
                                @ColumnResult(name = "PassengerFullName", type = String.class),
                                @ColumnResult(name = "PassengerEmail", type = String.class),
                                @ColumnResult(name = "FlightID", type = Integer.class),
                                @ColumnResult(name = "DepartureAirportCode", type = String.class),
                                @ColumnResult(name = "ArrivalAirportCode", type = String.class),
                                @ColumnResult(name = "DepartureTime", type = Timestamp.class),
                                @ColumnResult(name = "ArrivalTime", type = Timestamp.class),
                                @ColumnResult(name = "AircraftModel", type = String.class),
                                @ColumnResult(name = "AircraftTailNumber", type = String.class)
                        }
                )
        )
})

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Luggage")
public class Luggage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer luggageID;

    @ManyToOne
    @JoinColumn(name = "ticketID")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "checkInID")
    private CheckIns checkIn;

    private BigDecimal weight;
    private Boolean isExtra;
    private BigDecimal calculatedFee;
}