package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.entity.*;
import com.airportmanagement.airportmanagementsystem.repository.*;
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

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user) {
        user.setPasswordHash(user.getPasswordHash()); // hashleme eklenebilir
        userRepo.save(user);

        Role role = roleRepo.findByRoleName("PASSENGER").orElseThrow();
        UserRole userRole = UserRole.builder().user(user).role(role).build();
        userRoleRepo.save(userRole);

        return "redirect:/register?success";
    }
}
