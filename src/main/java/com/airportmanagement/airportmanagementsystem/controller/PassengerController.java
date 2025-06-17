package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.dto.FlightSearchDTO;
import com.airportmanagement.airportmanagementsystem.dto.PassengerBookingDTO;
import com.airportmanagement.airportmanagementsystem.dto.PassengerProfileDTO;
import com.airportmanagement.airportmanagementsystem.dto.SeatAvailabilityDTO;
import com.airportmanagement.airportmanagementsystem.dto.LuggageDetailsDTO;
import com.airportmanagement.airportmanagementsystem.entity.Airport;
import com.airportmanagement.airportmanagementsystem.entity.ApplicationSetting;
import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.entity.Notification;
import com.airportmanagement.airportmanagementsystem.repository.AirportRepository;
import com.airportmanagement.airportmanagementsystem.repository.FlightRepository;
import com.airportmanagement.airportmanagementsystem.repository.LuggageRepository;
import com.airportmanagement.airportmanagementsystem.repository.PassengerRepository;
import com.airportmanagement.airportmanagementsystem.repository.SeatRepository;
import com.airportmanagement.airportmanagementsystem.repository.TicketRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRoleRepository;
import com.airportmanagement.airportmanagementsystem.repository.CheckInRepository;
import com.airportmanagement.airportmanagementsystem.repository.ApplicationSettingRepository;
import com.airportmanagement.airportmanagementsystem.repository.NotificationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/passenger")
public class PassengerController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PassengerRepository passengerRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @Autowired
    private FlightRepository flightRepo;

    @Autowired
    private AirportRepository airportRepo;

    @Autowired
    private SeatRepository seatRepo;

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private CheckInRepository checkInRepo;

    @Autowired
    private ApplicationSettingRepository appSettingRepo;

    @Autowired
    private LuggageRepository luggageRepo;

    @Autowired
    private NotificationRepository notificationRepo;

    private boolean checkPassengerRole(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        return loggedUser != null && userRoleRepo.findByUserAndRole_RoleName(loggedUser, "PASSENGER").isPresent();
    }

    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public String showPassengerDashboard(HttpSession session, Model model) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }
        User loggedUser = (User) session.getAttribute("loggedUser");
        model.addAttribute("fullName", loggedUser.getFullName());

        long unreadNotificationsCount = notificationRepo.countByUser_UserIDAndIsReadFalse(loggedUser.getUserID());
        model.addAttribute("unreadNotificationsCount", unreadNotificationsCount);

        return "passenger-dashboard";
    }

    @GetMapping("/profile")
    @Transactional(readOnly = true)
    public String showProfile(HttpSession session, Model model) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }
        User loggedUser = (User) session.getAttribute("loggedUser");
        Optional<PassengerProfileDTO> profileOptional = userRepo.getPassengerProfile(loggedUser.getUserID());

        if (profileOptional.isPresent()) {
            model.addAttribute("profile", profileOptional.get());
            model.addAttribute("currentPasswordHash", loggedUser.getPasswordHash());
        } else {
            model.addAttribute("error", "Passenger profile not found.");
        }
        return "passenger-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam String passwordHash,
                                @RequestParam String phoneNumber,
                                @RequestParam String nationality,
                                @RequestParam String passportNumber,
                                RedirectAttributes redirectAttributes) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }
        User loggedUser = (User) session.getAttribute("loggedUser");

        try {
            Integer resultCode = userRepo.updatePassengerProfile(
                    loggedUser.getUserID(),
                    fullName,
                    email,
                    passwordHash,
                    phoneNumber,
                    nationality,
                    passportNumber
            );

            if (resultCode == 0) {

                User updatedUser = userRepo.findById(loggedUser.getUserID()).orElse(null);
                if (updatedUser != null) {
                    session.setAttribute("loggedUser", updatedUser);
                }
                redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            } else {
                String errorMessage;
                switch (resultCode) {
                    case -1: errorMessage = "User not found."; break;
                    case -2: errorMessage = "Email already in use by another account."; break;
                    default: errorMessage = "Failed to update profile. Error code: " + resultCode; break;
                }
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (DataAccessException e) {
            String specificDbMessage = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
            if (specificDbMessage != null && specificDbMessage.contains("duplicate key value") && specificDbMessage.contains("Email")) {
                redirectAttributes.addFlashAttribute("error", "The provided email is already in use by another account.");
            } else {
                redirectAttributes.addFlashAttribute("error", "A database error occurred during profile update. Please try again or contact support.");
            }
            e.printStackTrace();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/passenger/profile";
    }

    @GetMapping("/flights/search")
    @Transactional(readOnly = true)
    public String showFlightSearch(HttpSession session, Model model) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }
        List<Airport> airports = airportRepo.findAll();
        model.addAttribute("airports", airports);
        model.addAttribute("flights", List.of());
        return "passenger-search-flights";
    }

    @PostMapping("/flights/search")
    @Transactional(readOnly = true)
    public String searchFlights(HttpSession session,
                                @RequestParam(required = false) String departureAirportCode,
                                @RequestParam(required = false) String arrivalAirportCode,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
                                @RequestParam(defaultValue = "1") Integer minAvailableSeats,
                                Model model) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }

        if (departureDate != null && departureDate.isBefore(LocalDate.now())) {
            model.addAttribute("error", "Departure date cannot be in the past.");
            model.addAttribute("airports", airportRepo.findAll());
            model.addAttribute("departureAirportCode", departureAirportCode);
            model.addAttribute("arrivalAirportCode", arrivalAirportCode);
            model.addAttribute("departureDate", departureDate);
            model.addAttribute("minAvailableSeats", minAvailableSeats);
            return "passenger-search-flights";
        }

        LocalDateTime departureDateTime = departureDate != null ? departureDate.atStartOfDay() : null;

        List<FlightSearchDTO> flights = flightRepo.searchAvailableFlights(
                departureAirportCode,
                arrivalAirportCode,
                departureDateTime,
                minAvailableSeats
        );
        model.addAttribute("flights", flights);
        List<Airport> airports = airportRepo.findAll();
        model.addAttribute("airports", airports);

        model.addAttribute("departureAirportCode", departureAirportCode);
        model.addAttribute("arrivalAirportCode", arrivalAirportCode);
        model.addAttribute("departureDate", departureDate);
        model.addAttribute("minAvailableSeats", minAvailableSeats);

        return "passenger-search-flights";
    }

    @GetMapping("/flights/select-seat")
    @Transactional(readOnly = true)
    public String showSeatSelection(HttpSession session,
                                    @RequestParam Integer flightID,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }

        // Flight detaylarını bul (SP_SearchAvailableFlights'tan)
        List<FlightSearchDTO> flightDetailsList = flightRepo.searchAvailableFlights(null, null, null, 0); // Tüm uçuşları getir, sonra filtrele
        Optional<FlightSearchDTO> flightDetailsOptional = flightDetailsList.stream()
                .filter(f -> f.getFlightID().equals(flightID))
                .findFirst();

        if (flightDetailsOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Flight details not found.");
            return "redirect:/passenger/flights/search";
        }

        FlightSearchDTO flight = flightDetailsOptional.get();

        if (flight.getDepartureLocalDateTime().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "This flight has already departed and cannot be booked.");
            return "redirect:/passenger/flights/search";
        }

        List<SeatAvailabilityDTO> seats = seatRepo.getFlightAvailableSeats(flightID);

        List<SeatAvailabilityDTO> sortedSeats = seats.stream()
                .sorted(Comparator.comparing((SeatAvailabilityDTO s) -> {
                    try {
                        return Integer.parseInt(s.getSeatNumber().replaceAll("[^0-9]", ""));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }).thenComparing(SeatAvailabilityDTO::getSeatNumber))
                .collect(Collectors.toList());

        model.addAttribute("flight", flight);
        model.addAttribute("seats", sortedSeats);
        User loggedUser = (User) session.getAttribute("loggedUser");
        passengerRepo.findByUser(loggedUser).ifPresent(p -> model.addAttribute("passengerID", p.getPassengerID()));

        Optional<ApplicationSetting> appSettings = appSettingRepo.getApplicationSettings();
        model.addAttribute("standardLuggageWeightKg", appSettings.map(ApplicationSetting::getStandardLuggageWeightKg).orElse(20));
        model.addAttribute("extraLuggageFeePerKg", appSettings.map(ApplicationSetting::getExtraLuggageFeePerKg).orElse(BigDecimal.valueOf(5.00)));

        return "passenger-seat-selection";
    }

    @PostMapping("/book")
    public String bookTicket(HttpSession session,
                             @RequestParam Integer flightID,
                             @RequestParam Integer passengerID,
                             @RequestParam String seatNumber,
                             @RequestParam BigDecimal basePrice,
                             @RequestParam(required = false) BigDecimal luggageWeight,
                             @RequestParam(defaultValue = "false") Boolean isExtraLuggage,
                             RedirectAttributes redirectAttributes) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }

        if (passengerID == null) {
            redirectAttributes.addFlashAttribute("error", "Passenger information is missing. Please log in again.");
            return "redirect:/login";
        }

        try {
            Integer newTicketId = ticketRepo.bookTicket(passengerID, flightID, seatNumber, basePrice, luggageWeight, isExtraLuggage);

            if (newTicketId != null && newTicketId > 0) {
                redirectAttributes.addFlashAttribute("success", "Ticket booked successfully! Your Ticket ID: " + newTicketId);
                return "redirect:/passenger/my-bookings";
            } else {
                String errorMessage;
                switch (newTicketId) {
                    case -1: errorMessage = "Passenger not found."; break;
                    case -2: errorMessage = "Flight not found."; break;
                    case -3: errorMessage = "Flight is not active (e.g., Cancelled, Arrived)."; break;
                    case -4: errorMessage = "Invalid seat number for this flight."; break;
                    case -5: errorMessage = "Seat is already taken. Please select another seat."; break;
                    case -6: errorMessage = "Flight is full. Please select another flight."; break;
                    case -7: errorMessage = "Cannot book this flight: it has already departed."; break;
                    case -8: errorMessage = "Failed to add initial luggage. Please try again."; break;
                    default: errorMessage = "Failed to book ticket. Please try again. Error Code: " + newTicketId; break;
                }
                redirectAttributes.addFlashAttribute("error", errorMessage);

                return "redirect:/passenger/flights/select-seat?flightID=" + flightID;
            }
        } catch (DataAccessException e) {
            String specificDbMessage = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
            if (specificDbMessage != null && specificDbMessage.contains("Seat is already taken")) {
                redirectAttributes.addFlashAttribute("error", "Booking failed: The selected seat is already taken. Please choose another seat.");
            } else if (specificDbMessage != null && specificDbMessage.contains("Flight is full")) {
                redirectAttributes.addFlashAttribute("error", "Booking failed: The flight is full. Please select another flight.");
            } else {
                redirectAttributes.addFlashAttribute("error", "A database error occurred during booking. Please try again or contact support.");
            }
            e.printStackTrace();
            return "redirect:/passenger/flights/select-seat?flightID=" + flightID;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during booking: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/passenger/flights/select-seat?flightID=" + flightID;
        }
    }

    @GetMapping("/my-bookings")
    @Transactional(readOnly = true)
    public String showMyBookings(HttpSession session, Model model) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }
        User loggedUser = (User) session.getAttribute("loggedUser");
        List<PassengerBookingDTO> myBookings = ticketRepo.getPassengerBookings(loggedUser.getUserID());

        model.addAttribute("bookings", myBookings);
        return "passenger-my-bookings";
    }

    @GetMapping("/booking-details")
    @Transactional(readOnly = true)
    public String showBookingDetails(HttpSession session,
                                     @RequestParam Integer ticketID,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }
        User loggedUser = (User) session.getAttribute("loggedUser");

        Optional<PassengerBookingDTO> bookingDetailsOptional = ticketRepo.getPassengerBookings(loggedUser.getUserID())
                .stream()
                .filter(b -> b.getTicketID().equals(ticketID))
                .findFirst();

        if (bookingDetailsOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Booking details not found or you are not authorized to view it.");
            return "redirect:/passenger/my-bookings";
        }

        PassengerBookingDTO booking = bookingDetailsOptional.get();

        List<LuggageDetailsDTO> luggageItems = luggageRepo.getLuggageByTicketID(ticketID);
        model.addAttribute("luggageItems", luggageItems);
        model.addAttribute("ticketIDForLuggage", ticketID);

        LocalDateTime departureTime = booking.getDepartureLocalDateTime();
        LocalDateTime currentTime = LocalDateTime.now();

        Optional<ApplicationSetting> appSettings = appSettingRepo.getApplicationSettings();
        int maxCheckInHoursBefore = appSettings.map(ApplicationSetting::getMaximumCheckInHoursBeforeDeparture).orElse(24);
        int minCheckInMinutesBefore = appSettings.map(ApplicationSetting::getMinimumCheckInMinutesBeforeDeparture).orElse(60);

        boolean isCheckInAvailableNow = false;
        if (booking.getStatus().equals("Active") && !booking.isCheckedIn()) {
            LocalDateTime checkInOpenTime = departureTime.minusHours(maxCheckInHoursBefore);
            LocalDateTime checkInCloseTime = departureTime.minusMinutes(minCheckInMinutesBefore);
            isCheckInAvailableNow = (currentTime.isAfter(checkInOpenTime) && currentTime.isBefore(checkInCloseTime));
        }

        model.addAttribute("booking", booking);
        model.addAttribute("isCheckInAvailableNow", isCheckInAvailableNow);

        model.addAttribute("standardLuggageWeightKg", appSettings.map(ApplicationSetting::getStandardLuggageWeightKg).orElse(20));
        model.addAttribute("extraLuggageFeePerKg", appSettings.map(ApplicationSetting::getExtraLuggageFeePerKg).orElse(BigDecimal.valueOf(5.00)));

        boolean canCancelTicket = booking.getStatus().equals("Active")
                && booking.getDepartureLocalDateTime().isAfter(LocalDateTime.now().plusHours(12))
                && !booking.isCheckedIn();
        model.addAttribute("canCancelTicket", canCancelTicket);


        return "passenger-booking-details";
    }


    @PostMapping("/cancel-ticket")
    public String cancelTicket(HttpSession session,
                               @RequestParam Integer ticketID,
                               RedirectAttributes redirectAttributes) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }

        User loggedUser = (User) session.getAttribute("loggedUser");

        boolean ticketBelongsToUser = ticketRepo.findById(ticketID)
                .map(t -> t.getPassenger().getUser().getUserID().equals(loggedUser.getUserID()))
                .orElse(false);

        if (!ticketBelongsToUser) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized operation. Ticket not found or does not belong to you.");
            return "redirect:/passenger/my-bookings";
        }

        try {
            Integer resultCode = ticketRepo.cancelTicket(ticketID);

            if (resultCode == 0) {
                redirectAttributes.addFlashAttribute("success", "Ticket " + ticketID + " has been successfully cancelled!");
            } else {
                String errorMessage;
                switch (resultCode) {
                    case -1: errorMessage = "Ticket not found."; break;
                    case -2: errorMessage = "Ticket is already cancelled."; break;
                    default: errorMessage = "Failed to cancel ticket. Error Code: " + resultCode; break;
                }
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (DataAccessException e) {

            redirectAttributes.addFlashAttribute("error", "A database error occurred during ticket cancellation. Please try again or contact support.");
            e.printStackTrace();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during ticket cancellation: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/passenger/my-bookings";
    }

    @PostMapping("/check-in")
    public String performCheckIn(HttpSession session,
                                 @RequestParam Integer ticketID,
                                 RedirectAttributes redirectAttributes) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }

        User loggedUser = (User) session.getAttribute("loggedUser");
        // Check-in yapılacak biletin kullanıcının kendi bileti olduğunu doğrula (güvenlik)
        Optional<PassengerBookingDTO> bookingOptional = ticketRepo.getPassengerBookings(loggedUser.getUserID())
                .stream()
                .filter(b -> b.getTicketID().equals(ticketID))
                .findFirst();
        if (bookingOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized attempt to check-in for ticket " + ticketID);
            return "redirect:/passenger/my-bookings";
        }

        try {
            Integer resultCode = checkInRepo.performCheckIn(ticketID);

            if (resultCode == 0) {
                redirectAttributes.addFlashAttribute("success", "Check-in successful for Ticket ID: " + ticketID + "!");
            } else {
                String errorMessage;
                switch (resultCode) {
                    case -1: errorMessage = "Check-in failed: Ticket not found."; break;
                    case -2: errorMessage = "Check-in failed: You have already checked in for this ticket."; break;
                    case -3: errorMessage = "Check-in failed: Flight is not active (e.g., cancelled, departed, arrived)."; break;
                    case -4: errorMessage = "Check-in failed: Flight has already departed."; break;
                    case -5: errorMessage = "Check-in failed: Check-in is not yet open for this flight."; break;
                    case -6: errorMessage = "Check-in failed: Check-in window has closed for this flight."; break;
                    case -8: errorMessage = "Check-in failed: This ticket has been cancelled."; break; // İptal edilen bilet kontrolü
                    default: errorMessage = "Check-in failed. Please try again. Error Code: " + resultCode; break;
                }
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (DataAccessException e) {
            // SP'den veya trigger'dan gelebilecek spesifik DB hatalarını burada yakalayabiliriz.
            // Örn: concurrency hataları, kapı atama çakışmaları vb.
            redirectAttributes.addFlashAttribute("error", "A database error occurred during check-in. Please try again or contact support.");
            e.printStackTrace();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during check-in: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/passenger/my-bookings";
    }

    @PostMapping("/luggage/add")
    public String addLuggage(HttpSession session,
                             @RequestParam Integer ticketID,
                             @RequestParam BigDecimal weight,
                             @RequestParam(defaultValue = "false") Boolean isExtra,
                             RedirectAttributes redirectAttributes) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }

        User loggedUser = (User) session.getAttribute("loggedUser");
        // Bagaj eklenecek biletin kullanıcının kendi bileti olduğunu doğrula (güvenlik)
        boolean ticketBelongsToUser = ticketRepo.findById(ticketID)
                .map(t -> t.getPassenger().getUser().getUserID().equals(loggedUser.getUserID()))
                .orElse(false);

        if (!ticketBelongsToUser) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized luggage operation. Ticket record not found or does not belong to you.");
            return "redirect:/passenger/my-bookings";
        }

        try {
            Integer newLuggageID = luggageRepo.addLuggage(ticketID, weight, isExtra);

            if (newLuggageID != null && newLuggageID > 0) {
                redirectAttributes.addFlashAttribute("success", "Luggage added successfully! ID: " + newLuggageID);
            } else {
                String errorMessage;
                switch (newLuggageID) {
                    case -1: errorMessage = "Failed to add luggage: Ticket not found."; break;
                    case -2: errorMessage = "Failed to add luggage: Weight must be positive."; break;
                    default: errorMessage = "Failed to add luggage. Please try again. Error Code: " + newLuggageID; break;
                }
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "A database error occurred during luggage addition. Please try again or contact support.");
            e.printStackTrace();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during luggage addition: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/passenger/booking-details?ticketID=" + ticketID;
    }

    @PostMapping("/luggage/update")
    public String updateLuggage(HttpSession session,
                                @RequestParam Integer luggageID,
                                @RequestParam BigDecimal weight,
                                @RequestParam(defaultValue = "false") Boolean isExtra,
                                RedirectAttributes redirectAttributes) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }

        User loggedUser = (User) session.getAttribute("loggedUser");
        // Güncellenecek bagajın kullanıcının kendi biletine ait olduğunu doğrula (güvenlik)
        Optional<Integer> ticketIdForRedirectOpt = luggageRepo.findById(luggageID)
                .filter(l -> l.getTicket().getPassenger().getUser().getUserID().equals(loggedUser.getUserID()))
                .map(l -> l.getTicket().getTicketID());

        if (ticketIdForRedirectOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized luggage operation. Luggage record not found or does not belong to you.");
            return "redirect:/passenger/my-bookings";
        }
        Integer ticketIdForRedirect = ticketIdForRedirectOpt.get();

        try {
            Integer resultCode = luggageRepo.updateLuggage(luggageID, weight, isExtra);

            if (resultCode == 0) {
                redirectAttributes.addFlashAttribute("success", "Luggage updated successfully!");
            } else {
                String errorMessage;
                switch (resultCode) {
                    case -1: errorMessage = "Failed to update luggage: Luggage item not found."; break;
                    case -2: errorMessage = "Failed to update luggage: Weight must be positive."; break;
                    default: errorMessage = "Failed to update luggage. Please try again. Error Code: " + resultCode; break;
                }
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "A database error occurred during luggage update. Please try again or contact support.");
            e.printStackTrace();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during luggage update: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/passenger/booking-details?ticketID=" + ticketIdForRedirect;
    }

    @PostMapping("/luggage/delete")
    public String deleteLuggage(HttpSession session,
                                @RequestParam Integer luggageID,
                                RedirectAttributes redirectAttributes) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }

        User loggedUser = (User) session.getAttribute("loggedUser");
        // Silinecek bagajın kullanıcının kendi biletine ait olduğunu doğrula (güvenlik)
        Optional<Integer> ticketIdForRedirectOpt = luggageRepo.findById(luggageID)
                .filter(l -> l.getTicket().getPassenger().getUser().getUserID().equals(loggedUser.getUserID()))
                .map(l -> l.getTicket().getTicketID());

        if (ticketIdForRedirectOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized luggage operation. Luggage record not found or does not belong to you.");
            return "redirect:/passenger/my-bookings";
        }
        Integer ticketIdForRedirect = ticketIdForRedirectOpt.get();

        try {
            Integer resultCode = luggageRepo.deleteLuggage(luggageID);

            if (resultCode == 0) {
                redirectAttributes.addFlashAttribute("success", "Luggage deleted successfully!");
            } else {
                String errorMessage;
                switch (resultCode) {
                    case -1: errorMessage = "Failed to delete luggage: Luggage item not found."; break;
                    default: errorMessage = "Failed to delete luggage. Please try again. Error Code: " + resultCode; break;
                }
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "A database error occurred during luggage deletion. Please try again or contact support.");
            e.printStackTrace();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during luggage deletion: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/passenger/booking-details?ticketID=" + ticketIdForRedirect;
    }


    @GetMapping("/notifications")
    @Transactional(readOnly = true)
    public String showNotifications(HttpSession session, Model model) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }
        User loggedUser = (User) session.getAttribute("loggedUser");
        List<Notification> notifications = notificationRepo.getNotificationsForUser(loggedUser.getUserID());
        model.addAttribute("notifications", notifications);


        long unreadNotificationsCount = notificationRepo.countByUser_UserIDAndIsReadFalse(loggedUser.getUserID());
        model.addAttribute("unreadNotificationsCount", unreadNotificationsCount);

        return "passenger-notifications";
    }

    @PostMapping("/notifications/mark-read")
    public String markNotificationAsRead(HttpSession session,
                                         @RequestParam Integer notificationID,
                                         RedirectAttributes redirectAttributes) {
        if (!checkPassengerRole(session)) {
            return "redirect:/login?error=unauthorized";
        }
        User loggedUser = (User) session.getAttribute("loggedUser");

        Optional<Notification> notificationOptional = notificationRepo.findById(notificationID);
        if (notificationOptional.isEmpty() || !notificationOptional.get().getUser().getUserID().equals(loggedUser.getUserID())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized operation. Notification not found or does not belong to you.");
            return "redirect:/passenger/notifications";
        }

        try {
            Integer resultCode = notificationRepo.markNotificationAsRead(notificationID);

            if (resultCode == 0) {
                redirectAttributes.addFlashAttribute("success", "Notification marked as read!");
            } else {
                String errorMessage = "Failed to mark notification as read. Error Code: " + resultCode;
                if (resultCode == -1) errorMessage = "Notification not found.";
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "A database error occurred while marking notification as read. Please try again or contact support.");
            e.printStackTrace();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/passenger/notifications";
    }
}