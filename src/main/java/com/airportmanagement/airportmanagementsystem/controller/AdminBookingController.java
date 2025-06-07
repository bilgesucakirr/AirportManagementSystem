package com.airportmanagement.airportmanagementsystem.controller;

import com.airportmanagement.airportmanagementsystem.dto.BookingDetailsDTO;
import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.repository.TicketRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRoleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminBookingController {

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @GetMapping("/bookings")
    @Transactional(readOnly = true)
    public String listBookings(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !userRoleRepo.findByUserAndRole_RoleName(loggedUser, "ADMIN").isPresent()) {
            return "redirect:/login?error=unauthorized";
        }

        List<BookingDetailsDTO> bookings = ticketRepo.getAllBookingsDetails();
        model.addAttribute("bookings", bookings);
        return "admin-booking-overview";
    }
}