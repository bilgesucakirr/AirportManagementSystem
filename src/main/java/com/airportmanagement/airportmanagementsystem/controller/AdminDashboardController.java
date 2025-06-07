package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.repository.FlightRepository;
import com.airportmanagement.airportmanagementsystem.repository.PassengerRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRoleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PassengerRepository passengerRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @Autowired
    private FlightRepository flightRepo;

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        boolean isAdmin = userRoleRepo.findByUserAndRole_RoleName(loggedUser, "ADMIN").isPresent();
        if (!isAdmin) {
            return "redirect:/login?error=unauthorized";
        }

        model.addAttribute("totalUsers", userRepo.count());
        model.addAttribute("totalPassengers", passengerRepo.count());
        model.addAttribute("totalFlights", flightRepo.count());
        model.addAttribute("totalStaff", userRoleRepo.countByRole_RoleName("STAFF"));
        return "admin-dashboard";
    }
}