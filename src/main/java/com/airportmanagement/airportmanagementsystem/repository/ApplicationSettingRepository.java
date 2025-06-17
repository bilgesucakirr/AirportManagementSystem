package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.ApplicationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ApplicationSettingRepository extends JpaRepository<ApplicationSetting, Integer> {

    @Query(value = "EXEC sp_GetApplicationSettings", nativeQuery = true)
    Optional<ApplicationSetting> getApplicationSettings();

    @Procedure(procedureName = "sp_UpdateApplicationSettings", outputParameterName = "ResultCode")
    Integer updateApplicationSettings(@Param("BackupDirectory") String backupDirectory,
                                      @Param("EmailAlertsRecipient") String emailAlertsRecipient,
                                      @Param("ArchiveDataEnabled") Boolean archiveDataEnabled,
                                      @Param("ArchiveRetentionDays") Integer archiveRetentionDays,
                                      @Param("MinimumFlightCapacity") Integer minimumFlightCapacity,
                                      @Param("MinimumTurnaroundMinutes") Integer minimumTurnaroundMinutes,
                                      @Param("MaximumCheckInHoursBeforeDeparture") Integer maximumCheckInHoursBeforeDeparture,
                                      @Param("MinimumCheckInMinutesBeforeDeparture") Integer minimumCheckInMinutesBeforeDeparture,
                                      @Param("StandardLuggageWeightKg") Integer standardLuggageWeightKg,
                                      @Param("ExtraLuggageFeePerKg") BigDecimal extraLuggageFeePerKg,
                                      @Param("GateBufferBeforeDepartureMinutes") Integer gateBufferBeforeDepartureMinutes, // Yeni
                                      @Param("GateBufferAfterArrivalMinutes") Integer gateBufferAfterArrivalMinutes);    // Yeni
}