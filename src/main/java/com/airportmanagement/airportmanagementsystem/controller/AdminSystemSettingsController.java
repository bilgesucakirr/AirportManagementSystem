package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.entity.ApplicationSetting;
import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.repository.ApplicationSettingRepository;
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

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminSystemSettingsController {

    @Autowired
    private ApplicationSettingRepository appSettingRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @GetMapping("/settings")
    @Transactional(readOnly = true)
    public String showSystemSettings(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !userRoleRepo.findByUserAndRole_RoleName(loggedUser, "ADMIN").isPresent()) {
            return "redirect:/login?error=unauthorized";
        }

        ApplicationSetting settings = appSettingRepo.getApplicationSettings()
                .orElse(new ApplicationSetting());
        model.addAttribute("settings", settings);
        return "admin-system-settings";
    }


    @PostMapping("/settings/update")
    public String updateSystemSettings(@RequestParam String backupDirectory,
                                       @RequestParam String emailAlertsRecipient,
                                       @RequestParam(defaultValue = "false") Boolean archiveDataEnabled,
                                       @RequestParam Integer archiveRetentionDays,
                                       @RequestParam Integer minimumFlightCapacity,
                                       @RequestParam Integer minimumTurnaroundMinutes,
                                       @RequestParam Integer maximumCheckInHoursBeforeDeparture,
                                       @RequestParam Integer minimumCheckInMinutesBeforeDeparture,
                                       @RequestParam Integer standardLuggageWeightKg,
                                       @RequestParam BigDecimal extraLuggageFeePerKg,
                                       RedirectAttributes redirectAttributes) {
        Integer resultCode = 0;
        try {
            resultCode = appSettingRepo.updateApplicationSettings(
                    backupDirectory,
                    emailAlertsRecipient,
                    archiveDataEnabled,
                    archiveRetentionDays,
                    minimumFlightCapacity,
                    minimumTurnaroundMinutes,
                    maximumCheckInHoursBeforeDeparture,
                    minimumCheckInMinutesBeforeDeparture,
                    standardLuggageWeightKg,
                    extraLuggageFeePerKg
            );
            if (resultCode == 0) {
                redirectAttributes.addFlashAttribute("success", "System settings updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update system settings.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "redirect:/admin/settings";
    }
}