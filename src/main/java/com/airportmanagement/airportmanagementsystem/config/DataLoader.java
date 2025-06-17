// src/main/java/com/airportmanagement/airportmanagementsystem/config/DataLoader.java
package com.airportmanagement.airportmanagementsystem.config;

import com.airportmanagement.airportmanagementsystem.entity.ApplicationSetting;
import com.airportmanagement.airportmanagementsystem.entity.Passenger;
import com.airportmanagement.airportmanagementsystem.entity.Role;
import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.entity.UserRole;
import com.airportmanagement.airportmanagementsystem.entity.Aircraft;
import com.airportmanagement.airportmanagementsystem.entity.Airport; // Yeni
import com.airportmanagement.airportmanagementsystem.entity.Gate;     // Yeni
import com.airportmanagement.airportmanagementsystem.entity.Seat;
import com.airportmanagement.airportmanagementsystem.repository.ApplicationSettingRepository;
import com.airportmanagement.airportmanagementsystem.repository.PassengerRepository;
import com.airportmanagement.airportmanagementsystem.repository.RoleRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRoleRepository;
import com.airportmanagement.airportmanagementsystem.repository.AircraftRepository;
import com.airportmanagement.airportmanagementsystem.repository.AirportRepository; // Yeni
import com.airportmanagement.airportmanagementsystem.repository.GateRepository;     // Yeni
import com.airportmanagement.airportmanagementsystem.repository.SeatRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

//@Component
public class DataLoader {

//    @Autowired
//    private UserRepository userRepo;
//
//    @Autowired
//    private RoleRepository roleRepo;
//
//    @Autowired
//    private UserRoleRepository userRoleRepo;
//
//    @Autowired
//    private PassengerRepository passengerRepo;
//
//    @Autowired
//    private ApplicationSettingRepository appSettingRepo;
//
//    @Autowired
//    private AircraftRepository aircraftRepo;
//
//    @Autowired
//    private SeatRepository seatRepo;
//
//    @Autowired // Yeni
//    private AirportRepository airportRepo;
//
//    @Autowired // Yeni
//    private GateRepository gateRepo;
//
//    @PostConstruct
//    public void init() {
//        // --- Mevcut Roller ve Kullanıcılar ---
//        Role adminRole = roleRepo.findByRoleName("ADMIN").orElseGet(() -> {
//            Role newRole = Role.builder().roleName("ADMIN").build();
//            return roleRepo.save(newRole);
//        });
//
//        Role staffRole = roleRepo.findByRoleName("STAFF").orElseGet(() -> {
//            Role newRole = Role.builder().roleName("STAFF").build();
//            return roleRepo.save(newRole);
//        });
//
//        Role passengerRole = roleRepo.findByRoleName("PASSENGER").orElseGet(() -> {
//            Role newRole = Role.builder().roleName("PASSENGER").build();
//            return roleRepo.save(newRole);
//        });
//
//        Optional<User> adminUserOptional = userRepo.findByEmail("admin@thy.com");
//        if (adminUserOptional.isEmpty()) {
//            User adminUser = User.builder()
//                    .fullName("Admin User")
//                    .email("admin@thy.com")
//                    .passwordHash("admin123") // ! Düz metin parola, hashlenmeli !
//                    .phoneNumber("5551234567")
//                    .registrationDate(LocalDateTime.now())
//                    .build();
//            userRepo.save(adminUser);
//
//            UserRole adminUserRole = UserRole.builder().user(adminUser).role(adminRole).build();
//            userRoleRepo.save(adminUserRole);
//        }
//
//        Optional<User> staffUserOptional = userRepo.findByEmail("staff@thy.com");
//        if (staffUserOptional.isEmpty()) {
//            User staffUser = User.builder()
//                    .fullName("Staff Member")
//                    .email("staff@thy.com")
//                    .passwordHash("staff123") // ! Düz metin parola, hashlenmeli !
//                    .phoneNumber("5557654321")
//                    .registrationDate(LocalDateTime.now())
//                    .build();
//            userRepo.save(staffUser);
//
//            UserRole staffUserRole = UserRole.builder().user(staffUser).role(staffRole).build();
//            userRoleRepo.save(staffUserRole);
//        }
//
//        userRepo.findAll().forEach(user -> {
//            boolean isPassenger = userRoleRepo.findByUserAndRole_RoleName(user, "PASSENGER").isPresent();
//            if (isPassenger) {
//                boolean passengerEntryExists = passengerRepo.findByUser(user).isPresent();
//                if (!passengerEntryExists) {
//                    Passenger passenger = Passenger.builder()
//                            .user(user)
//                            .nationality(null)
//                            .passportNumber(null)
//                            .build();
//                    passengerRepo.save(passenger);
//                }
//            }
//        });
//
//        // --- Uygulama Ayarları ---
//        if (appSettingRepo.count() == 0) {
//            ApplicationSetting defaultSettings = ApplicationSetting.builder()
//                    .backupDirectory("C:\\SQLBackups\\AirportDb")
//                    .emailAlertsRecipient("admin@thy.com")
//                    .archiveDataEnabled(false)
//                    .archiveRetentionDays(365)
//                    .minimumFlightCapacity(100)
//                    .minimumTurnaroundMinutes(60)
//                    .maximumCheckInHoursBeforeDeparture(24)
//                    .minimumCheckInMinutesBeforeDeparture(60)
//                    .standardLuggageWeightKg(20)
//                    .extraLuggageFeePerKg(BigDecimal.valueOf(5.00))
//                    .build();
//            appSettingRepo.save(defaultSettings);
//        }
//
//        // --- Yeni: Havaalanı Verisi ---
//        if (airportRepo.count() == 0) {
//            airportRepo.save(Airport.builder().airportName("Ordu-Giresun Havalimani").location("Ordu, Giresun").code("OGU").airportType("Domestic").build());
//            airportRepo.save(Airport.builder().airportName("Istanbul Havalimani").location("Istanbul - Avrupa").code("IST").airportType("Domestic").build());
//            airportRepo.save(Airport.builder().airportName("Sabiha Gökçen Havalimani").location("Istanbul - Anadolu").code("SAW").airportType("Domestic").build()); // ID 4 olmalı
//            airportRepo.save(Airport.builder().airportName("Esenboga Havalimani").location("Ankara").code("ESB").airportType("Domestic").build()); // ID 5 olmalı
//            airportRepo.save(Airport.builder().airportName("Gaziantep Havalimani").location("Gaziantep").code("GZT").airportType("Domestic").build()); // ID 6 olmalı
//            airportRepo.save(Airport.builder().airportName("Atlanta Hartsfield-Jackson Airport").location("Atlanta, USA").code("ATL").airportType("International").build());
//            airportRepo.save(Airport.builder().airportName("Incheon International Airport").location("Seoul, South Korea").code("ICN").airportType("International").build());
//            airportRepo.save(Airport.builder().airportName("Haneda International Airport").location("Tokyo, JAPAN").code("HND").airportType("International").build());
//            airportRepo.save(Airport.builder().airportName("Sao Paulo Guarulhos Airport").location("Sao Paulo, Brasil").code("GRU").airportType("International").build());
//            System.out.println("Default airports added.");
//        }
//
//        // Kapı verisi (Her havaalanı için kapı ekleme)
//        if (gateRepo.count() == 0) {
//            Optional<Airport> orduGiresunAirport = airportRepo.findByCode("OGU");
//            Optional<Airport> istanbulAirport = airportRepo.findByCode("IST");
//            Optional<Airport> sabihaGokcenAirport = airportRepo.findByCode("SAW"); // SAW'ı alıyoruz
//            Optional<Airport> ankaraAirport = airportRepo.findByCode("ESB");
//            Optional<Airport> gaziantepAirport = airportRepo.findByCode("GZT");
//            Optional<Airport> atlantaAirport = airportRepo.findByCode("ATL");
//            Optional<Airport> incheonAirport = airportRepo.findByCode("ICN");
//            Optional<Airport> hanedaAirport = airportRepo.findByCode("HND");
//            Optional<Airport> saoPauloAirport = airportRepo.findByCode("GRU");
//
//
//            orduGiresunAirport.ifPresent(airport -> {
//                gateRepo.save(Gate.builder().gateCode("OGU-G1").airport(airport).build());
//                gateRepo.save(Gate.builder().gateCode("OGU-G2").airport(airport).build());
//                System.out.println("Gates added for OGU.");
//            });
//            istanbulAirport.ifPresent(airport -> {
//                gateRepo.save(Gate.builder().gateCode("IST-A1").airport(airport).build());
//                gateRepo.save(Gate.builder().gateCode("IST-A2").airport(airport).build());
//                gateRepo.save(Gate.builder().gateCode("IST-B1").airport(airport).build());
//                gateRepo.save(Gate.builder().gateCode("IST-B2").airport(airport).build());
//                System.out.println("Gates added for IST.");
//            });
//            sabihaGokcenAirport.ifPresent(airport -> { // **SAW için kapılar ekleniyor**
//                gateRepo.save(Gate.builder().gateCode("SAW-G1").airport(airport).build());
//                gateRepo.save(Gate.builder().gateCode("SAW-G2").airport(airport).build());
//                System.out.println("Gates added for SAW.");
//            });
//            ankaraAirport.ifPresent(airport -> {
//                gateRepo.save(Gate.builder().gateCode("ESB-C1").airport(airport).build());
//                gateRepo.save(Gate.builder().gateCode("ESB-C2").airport(airport).build());
//                System.out.println("Gates added for ESB.");
//            });
//            gaziantepAirport.ifPresent(airport -> {
//                gateRepo.save(Gate.builder().gateCode("GZT-G1").airport(airport).build());
//                gateRepo.save(Gate.builder().gateCode("GZT-G2").airport(airport).build());
//                System.out.println("Gates added for GZT.");
//            });
//            atlantaAirport.ifPresent(airport -> {
//                gateRepo.save(Gate.builder().gateCode("ATL-T1").airport(airport).build());
//                gateRepo.save(Gate.builder().gateCode("ATL-T2").airport(airport).build());
//                System.out.println("Gates added for ATL.");
//            });
//            incheonAirport.ifPresent(airport -> {
//                gateRepo.save(Gate.builder().gateCode("ICN-M1").airport(airport).build());
//                gateRepo.save(Gate.builder().gateCode("ICN-M2").airport(airport).build());
//                System.out.println("Gates added for ICN.");
//            });
//            hanedaAirport.ifPresent(airport -> {
//                gateRepo.save(Gate.builder().gateCode("HND-P1").airport(airport).build());
//                gateRepo.save(Gate.builder().gateCode("HND-P2").airport(airport).build());
//                System.out.println("Gates added for HND.");
//            });
//            saoPauloAirport.ifPresent(airport -> {
//                gateRepo.save(Gate.builder().gateCode("GRU-E1").airport(airport).build());
//                gateRepo.save(Gate.builder().gateCode("GRU-E2").airport(airport).build());
//                System.out.println("Gates added for GRU.");
//            });
//        }
//
//        // --- Uçak Verisi ---
//        List<Aircraft> allAircrafts = aircraftRepo.findAll();
//        if (allAircrafts.isEmpty()) {
//            Aircraft defaultAircraft = Aircraft.builder()
//                    .model("Airbus A320-200")
//                    .capacity(180)
//                    .tailNumber("TC-JCK")
//                    .rangeMiles(3300)
//                    .build();
//            aircraftRepo.save(defaultAircraft);
//            allAircrafts.add(defaultAircraft);
//
//            Aircraft defaultAircraft2 = Aircraft.builder()
//                    .model("Boeing 737-800")
//                    .capacity(189)
//                    .tailNumber("TC-JCS")
//                    .rangeMiles(3500)
//                    .build();
//            aircraftRepo.save(defaultAircraft2);
//            allAircrafts.add(defaultAircraft2);
//
//            System.out.println("Default aircrafts added.");
//        }

        // --- KOLTUK OLUŞTURMA (SQL TRIGGER'I VARSA BU KISMI KALDIR!) ---
        // Madem SQL'de TRG_AutoGenerateSeatsForNewAircraft trigger'ı var,
        // bu Java tarafındaki manuel koltuk oluşturma kodunu kaldırman önerilir.
        // DataLoader sadece Aircraft nesnesini kaydetmeli, koltuklar otomatik oluşmalı.
        /*
        for (Aircraft aircraft : allAircrafts) {
            if (seatRepo.findByAircraft_AircraftID(aircraft.getAircraftID()).isEmpty()) {
                generateSeatsForAircraft(aircraft);
            }
        }
        */
        // Bu metodun içeriğini de silebilirsin:
        // private void generateSeatsForAircraft(Aircraft aircraft) { ... }
    }

    // Eğer yukarıdaki koltuk oluşturma trigger'ı varsa bu metodu tamamen kaldır.
    /*
    private void generateSeatsForAircraft(Aircraft aircraft) {
        int totalCapacity = aircraft.getCapacity();

        int seatsPerFirstRow = 2;      // ör: A, F
        int seatsPerBusinessRow = 4;   // ör: A, B, E, F
        int seatsPerEconomyRow = 6;    // ör: A, B, C, D, E, F

        int totalRows = totalCapacity / seatsPerEconomyRow;
        int remainingSeats = totalCapacity % seatsPerEconomyRow;

        int firstRows = totalRows / 12; // Örnek oranlar
        int businessRows = (totalRows * 2) / 12;
        int economyRows = totalRows - firstRows - businessRows;

        int rowNum = 1;

        // First Class
        for (int i = 0; i < firstRows; i++) {
            for (char col : new char[]{'A', 'F'}) {
                seatRepo.save(Seat.builder()
                        .aircraft(aircraft)
                        .seatNumber(rowNum + String.valueOf(col))
                        .seatClass("First")
                        .build());
            }
            rowNum++;
        }
        // Business Class
        for (int i = 0; i < businessRows; i++) {
            for (char col : new char[]{'A', 'B', 'E', 'F'}) {
                seatRepo.save(Seat.builder()
                        .aircraft(aircraft)
                        .seatNumber(rowNum + String.valueOf(col))
                        .seatClass("Business")
                        .build());
            }
            rowNum++;
        }
        // Economy Class
        char[] economyCols = {'A', 'B', 'C', 'D', 'E', 'F'};
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
        // Remaining Economy Seats
        if (remainingSeats > 0) {
            for (int i = 0; i < remainingSeats; i++) {
                seatRepo.save(Seat.builder()
                        .aircraft(aircraft)
                        .seatNumber(rowNum + String.valueOf(economyCols[i]))
                        .seatClass("Economy")
                        .build());
            }
        }
        System.out.println("Generated seats for aircraft: " + aircraft.getModel() + " (ID: " + aircraft.getAircraftID() + ")");
    }
    */
//}