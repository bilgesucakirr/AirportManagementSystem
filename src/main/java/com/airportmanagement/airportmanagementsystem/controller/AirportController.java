package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.entity.Airport;
import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.repository.AirportRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AirportController {

    @Autowired
    private AirportRepository airportRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @GetMapping("/airports")
    @Transactional(readOnly = true)
    public String listAirports(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !userRoleRepo.findByUserAndRole_RoleName(loggedUser, "ADMIN").isPresent()) {
            return "redirect:/login?error=unauthorized";
        }

        List<Airport> allAirports = airportRepo.getAllAirports();
        List<Airport> domesticAirports = new ArrayList<>();
        List<Airport> internationalAirports = new ArrayList<>();

        for (Airport airport : allAirports) {
            if ("Domestic".equalsIgnoreCase(airport.getAirportType())) {
                domesticAirports.add(airport);
            } else if ("International".equalsIgnoreCase(airport.getAirportType())) {
                internationalAirports.add(airport);
            }
        }

        model.addAttribute("domesticAirports", domesticAirports);
        model.addAttribute("internationalAirports", internationalAirports);
        return "admin-airport";
    }

    @PostMapping("/airports/add")
    public String addAirport(@RequestParam String airportName,
                             @RequestParam String location,
                             @RequestParam String code,
                             @RequestParam String airportType,
                             RedirectAttributes redirectAttributes) {
        Integer newAirportID = 0;
        try {
            Integer result = airportRepo.addAirport(airportName, location, code, airportType);
            if (result == -2) {
                redirectAttributes.addFlashAttribute("error", "Airport with this code already exists!");
            } else if (result != null && result > 0) {
                redirectAttributes.addFlashAttribute("success", "Airport added successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to add airport.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/admin/airports";
    }

    @PostMapping("/airports/update")
    public String updateAirport(@RequestParam Integer airportID,
                                @RequestParam String airportName,
                                @RequestParam String location,
                                @RequestParam String code,
                                @RequestParam String airportType,
                                RedirectAttributes redirectAttributes) {
        Integer resultCode = 0;
        try {
            Integer result = airportRepo.updateAirport(airportID, airportName, location, code, airportType); // Yeni parametre eklendi
            if (result == -1) {
                redirectAttributes.addFlashAttribute("error", "Airport not found!");
            } else if (result == -2) {
                redirectAttributes.addFlashAttribute("error", "Another airport with this code already exists!");
            } else if (result == 0) {
                redirectAttributes.addFlashAttribute("success", "Airport updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update airport.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/admin/airports";
    }

    @PostMapping("/airports/delete")
    public String deleteAirport(@RequestParam Integer airportID,
                                RedirectAttributes redirectAttributes) {
        Integer resultCode = 0;
        try {
            Integer result = airportRepo.deleteAirport(airportID);
            if (result == -1) {
                redirectAttributes.addFlashAttribute("error", "Airport not found!");
            } else if (result == -4) {
                redirectAttributes.addFlashAttribute("error", "Cannot delete airport: it is referenced by existing routes!");
            } else if (result == -5) {
                redirectAttributes.addFlashAttribute("error", "Cannot delete airport: it has gates assigned to it!");
            }
            else if (result == 0) {
                redirectAttributes.addFlashAttribute("success", "Airport deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to delete airport.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/admin/airports";
    }
}