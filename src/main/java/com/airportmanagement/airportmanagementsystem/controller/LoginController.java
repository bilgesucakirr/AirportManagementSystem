package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.repository.UserRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRoleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        User user = userRepo.findByEmail(email).orElse(null);

        if (user != null && user.getPasswordHash().equals(password)) {
            session.setAttribute("loggedUser", user);

            if (userRoleRepo.findByUserAndRole_RoleName(user, "ADMIN").isPresent()) {
                return "redirect:/admin/dashboard";
            } else if (userRoleRepo.findByUserAndRole_RoleName(user, "STAFF").isPresent()) {
                return "redirect:/staff/dashboard";
            } else if (userRoleRepo.findByUserAndRole_RoleName(user, "PASSENGER").isPresent()) {
                return "redirect:/passenger/dashboard";
            } else {
                return "redirect:/";
            }

        } else {
            model.addAttribute("error", true);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}