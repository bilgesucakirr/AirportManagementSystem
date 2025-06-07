package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.entity.Aircraft;
import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.repository.AircraftRepository;
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

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AircraftController {

    @Autowired
    private AircraftRepository aircraftRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @GetMapping("/aircrafts")
    @Transactional(readOnly = true)
    public String listAircrafts(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !userRoleRepo.findByUserAndRole_RoleName(loggedUser, "ADMIN").isPresent()) {
            return "redirect:/login?error=unauthorized";
        }

        List<Aircraft> aircrafts = aircraftRepo.getAllAircrafts();
        model.addAttribute("aircrafts", aircrafts);
        return "admin-aircraft";
    }

    @PostMapping("/aircrafts/add")
    public String addAircraft(@RequestParam String model,
                              @RequestParam Integer capacity,
                              @RequestParam String tailNumber,
                              @RequestParam Integer rangeMiles,
                              RedirectAttributes redirectAttributes) {
        // Integer newAircraftID = 0; // Bu satır kaldırıldı
        try {
            Integer result = aircraftRepo.addAircraft(model, capacity, tailNumber, rangeMiles); // newAircraftID parametresi kaldırıldı
            if (result == -2) {
                redirectAttributes.addFlashAttribute("error", "Aircraft with this tail number already exists!");
            } else if (result != null && result > 0) {
                redirectAttributes.addFlashAttribute("success", "Aircraft added successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to add aircraft.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/admin/aircrafts";
    }

    @PostMapping("/aircrafts/update")
    public String updateAircraft(@RequestParam Integer aircraftID,
                                 @RequestParam String model,
                                 @RequestParam Integer capacity,
                                 @RequestParam String tailNumber,
                                 @RequestParam Integer rangeMiles,
                                 RedirectAttributes redirectAttributes) {

        try {
            Integer result = aircraftRepo.updateAircraft(aircraftID, model, capacity, tailNumber, rangeMiles); // resultCode parametresi kaldırıldı
            if (result == -1) {
                redirectAttributes.addFlashAttribute("error", "Aircraft not found!");
            } else if (result == -2) {
                redirectAttributes.addFlashAttribute("error", "Another aircraft with this tail number already exists!");
            } else if (result == 0) {
                redirectAttributes.addFlashAttribute("success", "Aircraft updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update aircraft.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/admin/aircrafts";
    }

    @PostMapping("/aircrafts/delete")
    public String deleteAircraft(@RequestParam Integer aircraftID,
                                 RedirectAttributes redirectAttributes) {

        try {
            Integer result = aircraftRepo.deleteAircraft(aircraftID);
            if (result == -1) {
                redirectAttributes.addFlashAttribute("error", "Aircraft not found!");
            } else if (result == -4) {
                redirectAttributes.addFlashAttribute("error", "Cannot delete aircraft: it is currently assigned to flights!");
            }
            else if (result == 0) {
                redirectAttributes.addFlashAttribute("success", "Aircraft deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to delete aircraft.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/admin/aircrafts";
    }
}