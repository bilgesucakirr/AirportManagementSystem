package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.dto.RouteDetailsDTO;
import com.airportmanagement.airportmanagementsystem.entity.Airport;
import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.repository.AirportRepository;
import com.airportmanagement.airportmanagementsystem.repository.RouteRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRoleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;

import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffRouteController {

    @Autowired
    private RouteRepository routeRepo;

    @Autowired
    private AirportRepository airportRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @GetMapping("/routes")
    @Transactional(readOnly = true)
    public String listRoutes(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !userRoleRepo.findByUserAndRole_RoleName(loggedUser, "STAFF").isPresent()) {
            return "redirect:/login?error=unauthorized";
        }

        List<RouteDetailsDTO> routes = routeRepo.findAllRoutesWithDetails();
        List<Airport> airports = airportRepo.findAll();

        model.addAttribute("routes", routes);
        model.addAttribute("airports", airports);
        return "staff-routes";
    }

    @PostMapping("/routes/add")
    public String addRoute(@RequestParam Integer departureAirportID,
                           @RequestParam Integer arrivalAirportID,
                           @RequestParam Integer estimatedDurationMinutes,
                           @RequestParam Integer distanceKm,
                           RedirectAttributes redirectAttributes) {
        try {
            Integer result = routeRepo.addRoute(departureAirportID, arrivalAirportID, estimatedDurationMinutes, distanceKm);
            if (result != null && result > 0) {
                redirectAttributes.addFlashAttribute("success", "Route added successfully!");
            } else {
                String errorMessage;
                switch (result) {
                    case -1: errorMessage = "Departure and arrival airports cannot be the same!"; break;
                    case -2: errorMessage = "Departure airport not found!"; break;
                    case -3: errorMessage = "Arrival airport not found!"; break;
                    case -4: errorMessage = "Route already exists between these airports!"; break;
                    default: errorMessage = "Failed to add route."; break;
                }
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/staff/routes";
    }

    @PostMapping("/routes/update")
    public String updateRoute(@RequestParam Integer routeID,
                              @RequestParam Integer departureAirportID,
                              @RequestParam Integer arrivalAirportID,
                              @RequestParam Integer estimatedDurationMinutes,
                              @RequestParam Integer distanceKm,
                              RedirectAttributes redirectAttributes) {
        try {
            Integer result = routeRepo.updateRoute(routeID, departureAirportID, arrivalAirportID, estimatedDurationMinutes, distanceKm);
            if (result == 0) {
                redirectAttributes.addFlashAttribute("success", "Route updated successfully!");
            } else {
                String errorMessage;
                switch (result) {
                    case -1: errorMessage = "Route not found!"; break;
                    case -2: errorMessage = "Departure and arrival airports cannot be the same!"; break;
                    case -3: errorMessage = "Departure airport not found!"; break;
                    case -4: errorMessage = "Arrival airport not found!"; break;
                    case -5: errorMessage = "Another route with these departure/arrival airports already exists!"; break;
                    default: errorMessage = "Failed to update route."; break;
                }
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (DataAccessException e) {
            String specificDbMessage = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
            if (specificDbMessage != null && specificDbMessage.contains("Cannot update route's departure or arrival airports. There are active flights currently assigned to this route.")) {
                redirectAttributes.addFlashAttribute("error", "Cannot update route: Active flights are assigned to this route. Modify flights first!");
            } else {
                redirectAttributes.addFlashAttribute("error", "A database error occurred during update. Please try again or contact support.");
            }
            e.printStackTrace();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
        }
        return "redirect:/staff/routes";
    }

    @PostMapping("/routes/delete")
    public String deleteRoute(@RequestParam Integer routeID,
                              RedirectAttributes redirectAttributes) {
        try {
            Integer result = routeRepo.deleteRoute(routeID);
            if (result == 0) {
                redirectAttributes.addFlashAttribute("success", "Route deleted successfully!");
            } else {
                String errorMessage;
                switch (result) {
                    case -1: errorMessage = "Route not found!"; break;
                    case -4: errorMessage = "Cannot delete route: it is referenced by existing flights!"; break;
                    default: errorMessage = "Failed to delete route."; break;
                }
                redirectAttributes.addFlashAttribute("error", errorMessage);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/staff/routes";
    }
}