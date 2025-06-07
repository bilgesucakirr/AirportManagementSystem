package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.repository.AircraftRepository;
import com.airportmanagement.airportmanagementsystem.repository.AirportRepository;
import com.airportmanagement.airportmanagementsystem.repository.FlightRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRoleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
public class StaffDashboardController {

    @Autowired
    private FlightRepository flightRepo;

    @Autowired
    private AirportRepository airportRepo;

    @Autowired
    private AircraftRepository aircraftRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @GetMapping("/staff/dashboard")
    public String showStaffDashboard(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        boolean isStaff = userRoleRepo.findByUserAndRole_RoleName(loggedUser, "STAFF").isPresent();
        if (!isStaff) {
            return "redirect:/login?error=unauthorized";
        }

        model.addAttribute("totalFlights", flightRepo.count());
        model.addAttribute("upcomingFlights", flightRepo.countByDepartureTimeAfter(LocalDateTime.now()));
        model.addAttribute("totalAirports", airportRepo.count());
        model.addAttribute("totalAircrafts", aircraftRepo.count());

        return "staff-dashboard";
    }
}