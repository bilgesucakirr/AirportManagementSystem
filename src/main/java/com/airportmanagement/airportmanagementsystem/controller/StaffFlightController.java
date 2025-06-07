package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.dto.FlightDetailsDTO;
import com.airportmanagement.airportmanagementsystem.entity.Aircraft;
import com.airportmanagement.airportmanagementsystem.entity.Route;
import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.repository.AircraftRepository;
import com.airportmanagement.airportmanagementsystem.repository.AirportRepository;
import com.airportmanagement.airportmanagementsystem.repository.FlightRepository;
import com.airportmanagement.airportmanagementsystem.repository.RouteRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRoleRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/staff")
public class StaffFlightController {

    @Autowired
    private FlightRepository flightRepo;

    @Autowired
    private RouteRepository routeRepo;

    @Autowired
    private AircraftRepository aircraftRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @GetMapping("/flights")
    @Transactional(readOnly = true)
    public String listFlights(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !userRoleRepo.findByUserAndRole_RoleName(loggedUser, "STAFF").isPresent()) {
            return "redirect:/login?error=unauthorized";
        }

        List<FlightDetailsDTO> flights = flightRepo.findAllFlightsWithDetails();
        List<Route> routes = routeRepo.findAll();
        List<Aircraft> allAircrafts = aircraftRepo.getAllAircrafts();

        model.addAttribute("flights", flights);
        model.addAttribute("routes", routes);
        model.addAttribute("aircrafts", allAircrafts);
        return "staff-flights";
    }

    @PostMapping("/flights/add")
    public String addFlight(@RequestParam Integer routeID,
                            @RequestParam Integer aircraftID,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalTime,
                            @RequestParam String status,
                            RedirectAttributes redirectAttributes) {
        try {
            Integer result = flightRepo.addFlight(routeID, aircraftID, departureTime, arrivalTime, status);
            if (result != null && result > 0) {
                redirectAttributes.addFlashAttribute("success", "Flight added successfully!");
            } else {
                String errorMessage;
                switch (result) {
                    case -1: errorMessage = "Route not found!"; break;
                    case -2: errorMessage = "Aircraft not found!"; break;
                    case -3: errorMessage = "Departure time must be before arrival time!"; break;
                    case -4: errorMessage = "Aircraft is already scheduled for an overlapping flight!"; break;
                    case -5: errorMessage = "Not enough turnaround time for the aircraft!"; break;
                    default: errorMessage = "Failed to add flight."; break;
                }
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
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
                               RedirectAttributes redirectAttributes) {
        try {
            Integer result = flightRepo.updateFlight(flightID, routeID, aircraftID, departureTime, arrivalTime, status);
            if (result == 0) {
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
                    default: errorMessage = "Failed to update flight."; break;
                }
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/staff/flights";
    }

    @PostMapping("/flights/delete")
    public String deleteFlight(@RequestParam Integer flightID,
                               RedirectAttributes redirectAttributes) {
        try {
            Integer result = flightRepo.deleteFlight(flightID);
            if (result == 0) {
                redirectAttributes.addFlashAttribute("success", "Flight deleted successfully!");
            } else {
                String errorMessage;
                switch (result) {
                    case -1: errorMessage = "Flight not found!"; break;
                    case -4: errorMessage = "Cannot delete flight: tickets are associated with it!"; break;
                    default: errorMessage = "Failed to delete flight."; break;
                }
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/staff/flights";
    }

    @GetMapping("/flights/getAircraftsByRouteDistance")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<Aircraft> getAircraftsByRouteDistance(@RequestParam Integer routeID) {
        Optional<Route> routeOptional = routeRepo.findById(routeID);

        if (routeOptional.isEmpty() || routeOptional.get().getDistanceKm() == null) {
            return List.of();
        }

        Integer routeDistanceKm = routeOptional.get().getDistanceKm();
        return aircraftRepo.getAircraftsByRouteDistance(routeDistanceKm);
    }
}