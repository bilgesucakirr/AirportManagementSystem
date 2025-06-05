package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.dto.UserWithRoleDTO;
import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !userRoleRepo.findByUserAndRole_RoleName(loggedUser, "ADMIN").isPresent()) {
            return "redirect:/login?error=unauthorized";
        }

        List<UserWithRoleDTO> allUsers = userRepo.findAllUsersWithRoles();

        List<UserWithRoleDTO> admins = allUsers.stream()
                .filter(u -> u.getRoles().contains("ADMIN"))
                .collect(Collectors.toList());

        List<UserWithRoleDTO> staff = allUsers.stream()
                .filter(u -> u.getRoles().contains("STAFF") && !u.getRoles().contains("ADMIN"))
                .collect(Collectors.toList());

        List<UserWithRoleDTO> passengers = allUsers.stream()
                .filter(u -> u.getRoles().contains("PASSENGER") && !u.getRoles().contains("ADMIN") && !u.getRoles().contains("STAFF"))
                .collect(Collectors.toList());

        model.addAttribute("admins", admins);
        model.addAttribute("staff", staff);
        model.addAttribute("passengers", passengers);
        return "admin-users";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam String fullName,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String phoneNumber,
                          @RequestParam String roleName,
                          RedirectAttributes redirectAttributes) {
        try {
            Integer result = userRepo.addUserAndAssignRole(fullName, email, password, phoneNumber, roleName);
            if (result == -1) {
                redirectAttributes.addFlashAttribute("error", "Role not found!");
            } else if (result == -2) {
                redirectAttributes.addFlashAttribute("error", "User with this email already exists!");
            } else if (result != null) {
                redirectAttributes.addFlashAttribute("success", "User added successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to add user.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/update")
    public String updateUser(@RequestParam Integer userID,
                             @RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String phoneNumber,
                             RedirectAttributes redirectAttributes) {
        try {
            Integer result = userRepo.updateUserDetails(userID, fullName, email, phoneNumber);
            if (result == -1) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
            } else if (result == -2) {
                redirectAttributes.addFlashAttribute("error", "Another user with this email already exists!");
            } else if (result != null) {
                redirectAttributes.addFlashAttribute("success", "User updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update user.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/remove-role")
    public String removeUserRole(@RequestParam Integer userID,
                                 @RequestParam String roleName,
                                 RedirectAttributes redirectAttributes) {
        try {
            Integer result = userRepo.removeUserRole(userID, roleName);
            if (result == -1) {
                redirectAttributes.addFlashAttribute("error", "Role not found or user does not have this role!");
            } else if (result != null) {
                redirectAttributes.addFlashAttribute("success", "Role removed successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to remove role.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete")
    public String deleteUserCompletely(@RequestParam Integer userID,
                                       RedirectAttributes redirectAttributes) {
        try {
            Integer result = userRepo.deleteUserCompletely(userID);
            if (result == -1) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
            } else if (result != null) {
                redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to delete user.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}