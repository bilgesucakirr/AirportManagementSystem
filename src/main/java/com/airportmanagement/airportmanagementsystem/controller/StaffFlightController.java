package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.dto.FlightDetailsDTO;
import com.airportmanagement.airportmanagementsystem.dto.GateAssignmentDetailsDTO;
import com.airportmanagement.airportmanagementsystem.entity.Aircraft;
import com.airportmanagement.airportmanagementsystem.entity.Airport;
import com.airportmanagement.airportmanagementsystem.entity.Gate;
import com.airportmanagement.airportmanagementsystem.entity.Route;
import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.repository.AircraftRepository;
import com.airportmanagement.airportmanagementsystem.repository.FlightRepository;
import com.airportmanagement.airportmanagementsystem.repository.RouteRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRoleRepository;
import com.airportmanagement.airportmanagementsystem.repository.GateAssignmentRepository;
import com.airportmanagement.airportmanagementsystem.repository.AirportRepository;
import com.airportmanagement.airportmanagementsystem.repository.GateRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Loglama için
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/staff")
public class StaffFlightController {

    private static final Logger logger = LoggerFactory.getLogger(StaffFlightController.class);

    @Autowired
    private FlightRepository flightRepo;

    @Autowired
    private RouteRepository routeRepo;

    @Autowired
    private AircraftRepository aircraftRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @Autowired
    private GateAssignmentRepository gateAssignmentRepo;

    @Autowired
    private AirportRepository airportRepo;

    @Autowired
    private GateRepository gateRepo;

    // Helper method to check staff role
    private boolean checkStaffRole(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Unauthorized access attempt: No user logged in.");
            return false;
        }
        boolean isStaff = userRoleRepo.findByUserAndRole_RoleName(loggedUser, "STAFF").isPresent();
        if (!isStaff) {
            logger.warn("Unauthorized access attempt for user {}: Not a STAFF role.", loggedUser.getEmail());
        }
        return isStaff;
    }

    @GetMapping("/flights")
    @Transactional(readOnly = true)
    public String listFlights(HttpSession session, Model model) {
        logger.info("Accessing /staff/flights endpoint. Session user check initiated.");
        if (!checkStaffRole(session)) {
            return "redirect:/login?error=unauthorized";
        }
        logger.debug("User is authorized as STAFF. Proceeding to fetch flight data.");

        try {
            List<FlightDetailsDTO> flights = flightRepo.findAllFlightsWithDetails();
            logger.info("Found {} flights with details.", flights.size());

            List<Route> routes = routeRepo.findAll();
            logger.info("Found {} routes for forms.", routes.size());

            List<Aircraft> allAircrafts = aircraftRepo.getAllAircrafts();
            logger.info("Found {} aircrafts for forms.", allAircrafts.size());

            List<Airport> allAirports = airportRepo.findAll();
            logger.info("Found {} airports for forms.", allAirports.size());

            List<Gate> allGates = gateRepo.findAll();
            logger.info("Found {} gates for forms.", allGates.size());

            model.addAttribute("flights", flights);
            model.addAttribute("routes", routes);
            model.addAttribute("aircrafts", allAircrafts);
            model.addAttribute("airports", allAirports);
            model.addAttribute("gates", allGates);

            logger.info("Successfully loaded flight management page.");
            return "staff-flights";
        } catch (Exception e) {
            logger.error("Error loading flight management page: {}", e.getMessage(), e);
            model.addAttribute("error", "An error occurred while loading flight data.");
            return "staff-flights"; // Hata durumunda bile sayfayı döndür, mesaj göster
        }
    }

    @PostMapping("/flights/add")
    public String addFlight(@RequestParam Integer routeID,
                            @RequestParam Integer aircraftID,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalTime,
                            @RequestParam String status,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) {
        logger.info("Attempting to add new flight. RouteID: {}, AircraftID: {}, DepartureTime: {}, Status: {}",
                routeID, aircraftID, departureTime, status);
        if (!checkStaffRole(session)) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/login?error=unauthorized";
        }

        try {
            Integer result = flightRepo.addFlight(routeID, aircraftID, departureTime, arrivalTime, status);
            if (result != null && result > 0) {
                logger.info("Flight added successfully. New Flight ID: {}", result);
                redirectAttributes.addFlashAttribute("success", "Flight added successfully!");
            } else {
                String errorMessage;
                switch (result) {
                    case -1: errorMessage = "Route not found!"; break;
                    case -2: errorMessage = "Aircraft not found!"; break;
                    case -3: errorMessage = "Departure time must be before arrival time!"; break;
                    case -4: errorMessage = "Aircraft is already scheduled for an overlapping flight!"; break;
                    case -5: errorMessage = "Not enough turnaround time for the aircraft!"; break;
                    default: errorMessage = "Failed to add flight. Error code: " + result; break;
                }
                logger.warn("Failed to add flight. Error code: {}. Message: {}", result, errorMessage);
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (DataAccessException e) {
            String specificDbMessage = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
            logger.error("Database error adding flight (RouteID: {}, AircraftID: {}): {}", routeID, aircraftID, specificDbMessage, e);
            if (specificDbMessage != null) {
                if (specificDbMessage.contains("Aircraft is already scheduled for an overlapping flight")) {
                    redirectAttributes.addFlashAttribute("error", "Flight addition failed: Aircraft is already scheduled for an overlapping flight.");
                } else if (specificDbMessage.contains("Not enough turnaround time for the aircraft")) {
                    redirectAttributes.addFlashAttribute("error", "Flight addition failed: Not enough turnaround time for the aircraft.");
                } else {
                    redirectAttributes.addFlashAttribute("error", "A database error occurred during addition. Please try again or contact support.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "An unknown database error occurred during flight addition.");
            }
        } catch (Exception e) {
            logger.error("Unexpected error adding flight (RouteID: {}, AircraftID: {}): {}", routeID, aircraftID, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
        }
        return "redirect:/staff/flights";
    }

    @PostMapping("/flights/update")
    public String updateFlight(@RequestParam Integer flightID,
                               @RequestParam Integer routeID,
                               @RequestParam Integer aircraftID,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalTime,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        logger.info("Attempting to update flight {}. RouteID: {}, AircraftID: {}, DepartureTime: {}, Status: {}",
                flightID, routeID, aircraftID, departureTime, status);
        if (!checkStaffRole(session)) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/login?error=unauthorized";
        }
        try {
            Integer result = flightRepo.updateFlight(flightID, routeID, aircraftID, departureTime, arrivalTime, status);
            if (result == 0) { // SP returns 0 for success
                logger.info("Flight {} updated successfully.", flightID);
                redirectAttributes.addFlashAttribute("success", "Flight updated successfully!");
            } else {
                String errorMessage;
                switch (result) {
                    case -1: errorMessage = "Flight not found!"; break;
                    case -2: errorMessage = "Route not found!"; break;
                    case -3: errorMessage = "Aircraft not found!"; break;
                    case -4: errorMessage = "Departure time must be before arrival time!"; break;
                    case -6: errorMessage = "Aircraft is already scheduled for an overlapping flight!"; break;
                    case -7: errorMessage = "Not enough turnaround time for the aircraft!"; break;
                    default: errorMessage = "Failed to update flight. Error code: " + result; break;
                }
                logger.warn("Failed to update flight {}. Error code: {}. Message: {}", flightID, result, errorMessage);
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (DataAccessException e) {
            String specificDbMessage = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
            logger.error("Database error updating flight {} (RouteID: {}, AircraftID: {}): {}", flightID, routeID, aircraftID, specificDbMessage, e);
            if (specificDbMessage != null) {
                if (specificDbMessage.contains("Cannot update flight details. The flight has already departed or arrived, or its departure time is being set to a past date/time.")) {
                    redirectAttributes.addFlashAttribute("error", "Cannot update flight: Departure time cannot be set to a past date/time, or the flight has already departed/arrived.");
                }
                else if (specificDbMessage.contains("Aircraft is already scheduled for an overlapping flight")) {
                    redirectAttributes.addFlashAttribute("error", "Flight update failed: Aircraft is already scheduled for an overlapping flight.");
                } else if (specificDbMessage.contains("Not enough turnaround time for the aircraft")) {
                    redirectAttributes.addFlashAttribute("error", "Flight update failed: Not enough turnaround time for the aircraft.");
                }
                else {
                    redirectAttributes.addFlashAttribute("error", "A database error occurred during update. Please try again or contact support.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "An unknown database error occurred during flight update.");
            }
        } catch (Exception e) {
            logger.error("Unexpected error updating flight {} (RouteID: {}, AircraftID: {}): {}", flightID, routeID, aircraftID, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
        }
        return "redirect:/staff/flights";
    }

    @PostMapping("/flights/delete")
    public String deleteFlight(@RequestParam Integer flightID,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        logger.info("Attempting to delete flight {}.", flightID);
        if (!checkStaffRole(session)) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/login?error=unauthorized";
        }
        try {
            Integer result = flightRepo.deleteFlight(flightID);
            if (result == 0) { // SP returns 0 for success
                logger.info("Flight {} deleted successfully.", flightID);
                redirectAttributes.addFlashAttribute("success", "Flight deleted successfully!");
            } else {
                String errorMessage;
                switch (result) {
                    case -1: errorMessage = "Flight not found!"; break;
                    case -4: errorMessage = "Cannot delete flight: tickets are associated with it!"; break;
                    default: errorMessage = "Failed to delete flight. Error code: " + result; break;
                }
                logger.warn("Failed to delete flight {}. Error code: {}. Message: {}", flightID, result, errorMessage);
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (DataAccessException e) {
            String specificDbMessage = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
            logger.error("Database error deleting flight {}: {}", flightID, specificDbMessage, e);
            if (specificDbMessage != null) {
                if (specificDbMessage.contains("foreign key constraint") && specificDbMessage.contains("Tickets")) {
                    redirectAttributes.addFlashAttribute("error", "Cannot delete flight: It has associated tickets. Please cancel/remove tickets first.");
                } else {
                    redirectAttributes.addFlashAttribute("error", "A database error occurred during deletion. Please try again or contact support.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "An unknown database error occurred during flight deletion.");
            }
        } catch (Exception e) {
            logger.error("Unexpected error deleting flight {}: {}", flightID, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
        }
        return "redirect:/staff/flights";
    }

    @GetMapping("/flights/getAircraftsByRouteDistance")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<Aircraft> getAircraftsByRouteDistance(@RequestParam Integer routeID, HttpSession session) {
        logger.debug("AJAX: Request to get aircrafts by route distance for RouteID: {}", routeID);
        if (!checkStaffRole(session)) {
            logger.warn("Unauthorized AJAX access to /getAircraftsByRouteDistance. Returning empty list.");
            return List.of();
        }

        Optional<Route> routeOptional = routeRepo.findById(routeID);

        if (routeOptional.isEmpty() || routeOptional.get().getDistanceKm() == null) {
            logger.warn("Route with ID {} not found or DistanceKm is null. Returning empty aircraft list.", routeID);
            return List.of();
        }

        Integer routeDistanceKm = routeOptional.get().getDistanceKm();
        List<Aircraft> suitableAircrafts = aircraftRepo.getAircraftsByRouteDistance(routeDistanceKm);
        logger.debug("Found {} suitable aircrafts for route ID {}.", suitableAircrafts.size(), routeID);
        return suitableAircrafts;
    }

    // Kapı Atama İşlemleri
    @PostMapping("/gate-assignments/assign")
    public String assignGateToFlight(@RequestParam Integer flightID,
                                     @RequestParam Integer gateID,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime assignmentStartTime,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime assignmentEndTime,
                                     RedirectAttributes redirectAttributes,
                                     HttpSession session) {
        logger.info("Attempting to assign gate {}. to Flight {}. From {} to {}.", gateID, flightID, assignmentStartTime, assignmentEndTime);
        if (!checkStaffRole(session)) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/login?error=unauthorized";
        }
        try {
            Integer newAssignmentID = gateAssignmentRepo.assignGateToFlight(flightID, gateID, assignmentStartTime, assignmentEndTime);
            if (newAssignmentID != null && newAssignmentID > 0) {
                logger.info("Gate {} assigned successfully to Flight {}. New Assignment ID: {}", gateID, flightID, newAssignmentID);
                redirectAttributes.addFlashAttribute("success", "Gate assigned successfully! Assignment ID: " + newAssignmentID);
            } else {
                String errorMessage;
                switch (newAssignmentID) {
                    case -1: errorMessage = "Flight not found."; break;
                    case -2: errorMessage = "Flight is not active (e.g., Cancelled, Arrived) for gate assignment."; break;
                    case -3: errorMessage = "Gate not found."; break;
                    case -4: errorMessage = "Gate does not belong to the flight's airport."; break;
                    case -5: errorMessage = "Assignment start time must be before end time."; break;
                    case -6: errorMessage = "Gate is already occupied for the selected time period."; break;
                    default: errorMessage = "Failed to assign gate. Error Code: " + newAssignmentID; break;
                }
                logger.warn("Failed to assign gate {} to Flight {}. Error code: {}. Message: {}", gateID, flightID, newAssignmentID, errorMessage);
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (DataAccessException e) {
            String specificDbMessage = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
            logger.error("Database error assigning gate {} to Flight {}: {}", gateID, flightID, specificDbMessage, e);
            if (specificDbMessage != null && specificDbMessage.contains("Gate is already occupied for the selected time period.")) {
                redirectAttributes.addFlashAttribute("error", "Gate assignment failed: The selected gate is already in use for the chosen time.");
            } else {
                redirectAttributes.addFlashAttribute("error", "A database error occurred during gate assignment. Please try again or contact support.");
            }
        } catch (Exception e) {
            logger.error("Unexpected error assigning gate {} to Flight {}: {}", gateID, flightID, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during gate assignment: " + e.getMessage());
        }
        return "redirect:/staff/flights";
    }

    @PostMapping("/gate-assignments/update")
    public String updateGateAssignment(@RequestParam Integer assignmentID,
                                       @RequestParam Integer newFlightID,
                                       @RequestParam Integer newGateID,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newAssignmentStartTime,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newAssignmentEndTime,
                                       RedirectAttributes redirectAttributes,
                                       HttpSession session) {
        logger.info("Attempting to update gate assignment {}. New FlightID: {}, New GateID: {}. From {} to {}.", assignmentID, newFlightID, newGateID, newAssignmentStartTime, newAssignmentEndTime);
        if (!checkStaffRole(session)) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/login?error=unauthorized";
        }
        try {
            Integer resultCode = gateAssignmentRepo.updateGateAssignment(assignmentID, newFlightID, newGateID, newAssignmentStartTime, newAssignmentEndTime);
            if (resultCode == 0) {
                logger.info("Gate assignment {} updated successfully.", assignmentID);
                redirectAttributes.addFlashAttribute("success", "Gate assignment updated successfully!");
            } else {
                String errorMessage;
                switch (resultCode) {
                    case -1: errorMessage = "Gate assignment not found."; break;
                    case -2: errorMessage = "New flight not found."; break;
                    case -3: errorMessage = "New gate not found."; break;
                    case -4: errorMessage = "New gate does not belong to the new flight's airport."; break;
                    case -5: errorMessage = "New assignment start time must be before end time."; break;
                    case -6: errorMessage = "New gate is already occupied for the selected time period."; break;
                    default: errorMessage = "Failed to update gate assignment. Error Code: " + resultCode; break;
                }
                logger.warn("Failed to update gate assignment {}. Error code: {}. Message: {}", assignmentID, resultCode, errorMessage);
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (DataAccessException e) {
            String specificDbMessage = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
            logger.error("Database error updating gate assignment {}: {}", assignmentID, specificDbMessage, e);
            if (specificDbMessage != null && specificDbMessage.contains("Gate is already assigned to another flight for an overlapping time period.")) {
                redirectAttributes.addFlashAttribute("error", "Gate assignment update failed: The selected gate is already in use for the chosen time.");
            } else {
                redirectAttributes.addFlashAttribute("error", "A database error occurred during gate assignment update. Please try again or contact support.");
            }
        } catch (Exception e) {
            logger.error("Unexpected error updating gate assignment {}: {}", assignmentID, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during gate assignment update: " + e.getMessage());
        }
        return "redirect:/staff/flights";
    }

    @PostMapping("/gate-assignments/delete")
    public String removeGateAssignment(@RequestParam Integer assignmentID,
                                       RedirectAttributes redirectAttributes,
                                       HttpSession session) {
        logger.info("Attempting to remove gate assignment {}.", assignmentID);
        if (!checkStaffRole(session)) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/login?error=unauthorized";
        }
        try {
            Integer resultCode = gateAssignmentRepo.removeGateAssignment(assignmentID);
            if (resultCode == 0) {
                logger.info("Gate assignment {} removed successfully.", assignmentID);
                redirectAttributes.addFlashAttribute("success", "Gate assignment removed successfully!");
            } else {
                String errorMessage;
                switch (resultCode) {
                    case -1: errorMessage = "Gate assignment not found."; break;
                    default: errorMessage = "Failed to remove gate assignment. Error Code: " + resultCode; break;
                }
                logger.warn("Failed to remove gate assignment {}. Error code: {}. Message: {}", assignmentID, resultCode, errorMessage);
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (Exception e) {
            logger.error("Unexpected error removing gate assignment {}: {}", assignmentID, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during gate assignment removal: " + e.getMessage());
        }
        return "redirect:/staff/flights";
    }

    // Belirli bir havaalanı ve zaman aralığı için müsait kapıları getiren AJAX endpoint'i
    @GetMapping("/gate-assignments/getAvailableGates")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<Gate> getAvailableGates(@RequestParam Integer airportID,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desiredStartTime,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desiredEndTime,
                                        HttpSession session) {
        logger.debug("AJAX: Request to get available gates for AirportID: {}, StartTime: {}, EndTime: {}", airportID, desiredStartTime, desiredEndTime);
        if (!checkStaffRole(session)) {
            logger.warn("Unauthorized AJAX access to /getAvailableGates. Returning empty list.");
            return List.of();
        }
        try {
            List<Gate> availableGates = gateAssignmentRepo.getAvailableGatesForPeriod(airportID, desiredStartTime, desiredEndTime);
            logger.debug("Found {} available gates for AirportID: {}.", availableGates.size(), airportID);
            return availableGates;
        } catch (Exception e) {
            logger.error("Error retrieving available gates for AirportID: {}: {}", airportID, e.getMessage(), e);
            // Frontend'e boş liste dönmek yerine, daha anlamlı bir hata yanıtı da döndürülebilir.
            return List.of();
        }
    }

    // Tüm kapı atamalarını listelemek için AJAX endpoint'i
    @GetMapping("/gate-assignments/all")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<GateAssignmentDetailsDTO> getAllGateAssignments(HttpSession session) {
        logger.debug("AJAX: Request to get all gate assignments details.");
        if (!checkStaffRole(session)) {
            logger.warn("Unauthorized AJAX access to /gate-assignments/all. Returning empty list.");
            return List.of();
        }
        try {
            List<GateAssignmentDetailsDTO> assignments = gateAssignmentRepo.getAllGateAssignmentsWithDetails();
            logger.debug("Found {} total gate assignments.", assignments.size());
            return assignments;
        } catch (Exception e) {
            logger.error("Error retrieving all gate assignments: {}", e.getMessage(), e);
            return List.of();
        }
    }
}