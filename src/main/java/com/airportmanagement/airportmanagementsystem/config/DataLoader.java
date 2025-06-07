package com.airportmanagement.airportmanagementsystem.config;

import com.airportmanagement.airportmanagementsystem.entity.ApplicationSetting;
import com.airportmanagement.airportmanagementsystem.entity.Passenger;
import com.airportmanagement.airportmanagementsystem.entity.Role;
import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.entity.UserRole;
import com.airportmanagement.airportmanagementsystem.entity.Aircraft;
import com.airportmanagement.airportmanagementsystem.entity.Seat;
import com.airportmanagement.airportmanagementsystem.repository.ApplicationSettingRepository;
import com.airportmanagement.airportmanagementsystem.repository.PassengerRepository;
import com.airportmanagement.airportmanagementsystem.repository.RoleRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRoleRepository;
import com.airportmanagement.airportmanagementsystem.repository.AircraftRepository;
import com.airportmanagement.airportmanagementsystem.repository.SeatRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Component
public class DataLoader {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @Autowired
    private PassengerRepository passengerRepo;

    @Autowired
    private ApplicationSettingRepository appSettingRepo;

    @Autowired
    private AircraftRepository aircraftRepo;

    @Autowired
    private SeatRepository seatRepo;


    @PostConstruct
    public void init() {
        Role adminRole = roleRepo.findByRoleName("ADMIN").orElseGet(() -> {
            Role newRole = Role.builder().roleName("ADMIN").build();
            return roleRepo.save(newRole);
        });

        Role staffRole = roleRepo.findByRoleName("STAFF").orElseGet(() -> {
            Role newRole = Role.builder().roleName("STAFF").build();
            return roleRepo.save(newRole);
        });

        Role passengerRole = roleRepo.findByRoleName("PASSENGER").orElseGet(() -> {
            Role newRole = Role.builder().roleName("PASSENGER").build();
            return roleRepo.save(newRole);
        });

        Optional<User> adminUserOptional = userRepo.findByEmail("admin@thy.com");
        if (adminUserOptional.isEmpty()) {
            User adminUser = User.builder()
                    .fullName("Admin User")
                    .email("admin@thy.com")
                    .passwordHash("admin123")
                    .phoneNumber("5551234567")
                    .registrationDate(LocalDateTime.now())
                    .build();
            userRepo.save(adminUser);

            UserRole adminUserRole = UserRole.builder().user(adminUser).role(adminRole).build();
            userRoleRepo.save(adminUserRole);
        }

        Optional<User> staffUserOptional = userRepo.findByEmail("staff@thy.com");
        if (staffUserOptional.isEmpty()) {
            User staffUser = User.builder()
                    .fullName("Staff Member")
                    .email("staff@thy.com")
                    .passwordHash("staff123")
                    .phoneNumber("5557654321")
                    .registrationDate(LocalDateTime.now())
                    .build();
            userRepo.save(staffUser);

            UserRole staffUserRole = UserRole.builder().user(staffUser).role(staffRole).build();
            userRoleRepo.save(staffUserRole);
        }

        userRepo.findAll().forEach(user -> {
            boolean isPassenger = userRoleRepo.findByUserAndRole_RoleName(user, "PASSENGER").isPresent();
            if (isPassenger) {
                boolean passengerEntryExists = passengerRepo.findByUser(user).isPresent();
                if (!passengerEntryExists) {
                    Passenger passenger = Passenger.builder()
                            .user(user)
                            .nationality(null)
                            .passportNumber(null)
                            .build();
                    passengerRepo.save(passenger);
                }
            }
        });

        if (appSettingRepo.count() == 0) {
            ApplicationSetting defaultSettings = ApplicationSetting.builder()
                    .backupDirectory("C:\\SQLBackups\\AirportDb")
                    .emailAlertsRecipient("admin@thy.com")
                    .archiveDataEnabled(false)
                    .archiveRetentionDays(365)
                    .minimumFlightCapacity(100)
                    .minimumTurnaroundMinutes(60)
                    .maximumCheckInHoursBeforeDeparture(24)
                    .minimumCheckInMinutesBeforeDeparture(60)
                    .standardLuggageWeightKg(20)
                    .extraLuggageFeePerKg(BigDecimal.valueOf(5.00))
                    .build();
            appSettingRepo.save(defaultSettings);
        }

        List<Aircraft> allAircrafts = aircraftRepo.findAll();
        if (allAircrafts.isEmpty()) {
            Aircraft defaultAircraft = Aircraft.builder()
                    .model("Airbus A320-200")
                    .capacity(180)
                    .tailNumber("TC-JCK")
                    .rangeMiles(3300)
                    .build();
            aircraftRepo.save(defaultAircraft);
            allAircrafts.add(defaultAircraft);
        }

        for (Aircraft aircraft : allAircrafts) {
            if (seatRepo.findByAircraft_AircraftID(aircraft.getAircraftID()).isEmpty()) {
                generateSeatsForAircraft(aircraft);
            }
        }
    }

    private void generateSeatsForAircraft(Aircraft aircraft) {
        int totalCapacity = aircraft.getCapacity();

        int seatsPerFirstRow = 2;      // ör: A, F
        int seatsPerBusinessRow = 4;   // ör: A, B, E, F
        int seatsPerEconomyRow = 6;    // ör: A, B, C, D, E, F

        int totalRows = totalCapacity / seatsPerEconomyRow;
        int remainingSeats = totalCapacity % seatsPerEconomyRow;

        int firstRows = totalRows / 12;
        int businessRows = (totalRows * 2) / 12;
        int economyRows = totalRows - firstRows - businessRows;

        int extraEconomySeats = remainingSeats;

        char[] firstCols = {'A', 'F'};
        char[] businessCols = {'A', 'B', 'E', 'F'};
        char[] economyCols = {'A', 'B', 'C', 'D', 'E', 'F'};

        int rowNum = 1;

        for (int i = 0; i < firstRows; i++) {
            for (char col : firstCols) {
                seatRepo.save(Seat.builder()
                        .aircraft(aircraft)
                        .seatNumber(rowNum + String.valueOf(col))
                        .seatClass("First")
                        .build());
            }
            rowNum++;
        }

        for (int i = 0; i < businessRows; i++) {
            for (char col : businessCols) {
                seatRepo.save(Seat.builder()
                        .aircraft(aircraft)
                        .seatNumber(rowNum + String.valueOf(col))
                        .seatClass("Business")
                        .build());
            }
            rowNum++;
        }

        for (int i = 0; i < economyRows; i++) {
            for (char col : economyCols) {
                seatRepo.save(Seat.builder()
                        .aircraft(aircraft)
                        .seatNumber(rowNum + String.valueOf(col))
                        .seatClass("Economy")
                        .build());
            }
            rowNum++;
        }

        if (extraEconomySeats > 0) {
            for (int i = 0; i < extraEconomySeats; i++) {
                seatRepo.save(Seat.builder()
                        .aircraft(aircraft)
                        .seatNumber(rowNum + String.valueOf(economyCols[i]))
                        .seatClass("Economy")
                        .build());
            }
        }

        int totalGenerated = (firstRows * seatsPerFirstRow) + (businessRows * seatsPerBusinessRow) + (economyRows * seatsPerEconomyRow) + extraEconomySeats;
        System.out.println("Generated " + totalGenerated + " seats for aircraft: " + aircraft.getModel() + " (ID: " + aircraft.getAircraftID() + ")");
    }
}