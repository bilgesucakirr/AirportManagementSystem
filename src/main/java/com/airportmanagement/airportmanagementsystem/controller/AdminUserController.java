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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @GetMapping("/users")
    @Transactional(readOnly = true)
    public String listUsers(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !userRoleRepo.findByUserAndRole_RoleName(loggedUser, "ADMIN").isPresent()) {
            return "redirect:/login?error=unauthorized";
        }

        List<UserWithRoleDTO> allUsersWithRoles = userRepo.findAllUsersWithRoles();

        List<User> admins = new ArrayList<>();
        List<User> staff = new ArrayList<>();
        List<User> passengers = new ArrayList<>();

        for (UserWithRoleDTO dto : allUsersWithRoles) {
            User user = User.builder()
                    .userID(dto.getUserID())
                    .fullName(dto.getFullName())
                    .email(dto.getEmail())
                    .phoneNumber(dto.getPhoneNumber())
                    .registrationDate(dto.getRegistrationDate() != null ? dto.getRegistrationDate().toLocalDateTime() : null)
                    .build();

            if (dto.getRoles().contains("ADMIN")) {
                admins.add(user);
            }
            if (dto.getRoles().contains("STAFF")) {
                staff.add(user);
            }
            if (dto.getRoles().contains("PASSENGER")) {
                passengers.add(user);
            }
        }

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

        Integer result = userRepo.addAndAssignRole(fullName, email, password, phoneNumber, roleName); // newUserID parametresi kaldırıldı

        if (result == -1) {
            redirectAttributes.addFlashAttribute("error", "Role not found.");
        } else if (result == -2) {
            redirectAttributes.addFlashAttribute("error", "User with this email already exists!");
        } else if (result != null && result > 0) {
            redirectAttributes.addFlashAttribute("success", "User added and role assigned successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to add user.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/update")
    public String updateUser(@RequestParam Integer userID,
                             @RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String phoneNumber,
                             RedirectAttributes redirectAttributes) {

        Integer result = userRepo.updateUserDetails(userID, fullName, email, phoneNumber);

        if (result == -1) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
        } else if (result == -2) {
            redirectAttributes.addFlashAttribute("error", "Another user with this email already exists!");
        } else if (result == 0) {
            redirectAttributes.addFlashAttribute("success", "User updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update user.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/remove-role")
    public String removeUserRole(@RequestParam Integer userID,
                                 @RequestParam String roleName,
                                 RedirectAttributes redirectAttributes) {

        Integer result = userRepo.removeUserRole(userID, roleName);

        if (result == -1) {
            redirectAttributes.addFlashAttribute("error", "Role not found!");
        } else if (result == 0) {
            redirectAttributes.addFlashAttribute("success", roleName + " role removed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to remove " + roleName + " role.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete")
    public String deleteUserCompletely(@RequestParam Integer userID,
                                       RedirectAttributes redirectAttributes) {

        Integer result = userRepo.deleteUserCompletely(userID);

        if (result == -1) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
        } else if (result == 0) {
            redirectAttributes.addFlashAttribute("success", "User deleted completely!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user.");
        }
        return "redirect:/admin/users";
    }
}