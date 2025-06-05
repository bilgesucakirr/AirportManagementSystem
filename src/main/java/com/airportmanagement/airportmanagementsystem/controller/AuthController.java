package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.entity.Passenger;
import com.airportmanagement.airportmanagementsystem.entity.Role;
import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.entity.UserRole;
import com.airportmanagement.airportmanagementsystem.repository.PassengerRepository;
import com.airportmanagement.airportmanagementsystem.repository.RoleRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @Autowired
    private PassengerRepository passengerRepo;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user) {
        user.setPasswordHash(user.getPasswordHash());
        userRepo.save(user);

        Role passengerRole = roleRepo.findByRoleName("PASSENGER").orElseThrow(() -> new RuntimeException("PASSENGER role not found!"));
        UserRole userRole = UserRole.builder().user(user).role(passengerRole).build();
        userRoleRepo.save(userRole);

        Passenger passenger = Passenger.builder()
                .user(user)
                .nationality(null)
                .passportNumber(null)
                .build();
        passengerRepo.save(passenger);

        return "redirect:/register?success";
    }
}