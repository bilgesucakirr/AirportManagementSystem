package com.airportmanagement.airportmanagementsystem.config;

import com.airportmanagement.airportmanagementsystem.entity.Passenger;
import com.airportmanagement.airportmanagementsystem.entity.Role;
import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.entity.UserRole;
import com.airportmanagement.airportmanagementsystem.repository.PassengerRepository;
import com.airportmanagement.airportmanagementsystem.repository.RoleRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRepository;
import com.airportmanagement.airportmanagementsystem.repository.UserRoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataLoader {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @Autowired
    private PassengerRepository passengerRepo;

    @PostConstruct
    public void init() {
        Role adminRole = roleRepo.findByRoleName("ADMIN").orElseGet(() -> {
            Role newRole = Role.builder().roleName("ADMIN").build();
            return roleRepo.save(newRole);
        });

        Role staffRole = roleRepo.findByRoleName("STAFF").orElseGet(() -> {
            Role newRole = Role.builder().roleName("STAFF").build();
            return roleRepo.save(newRole);
        });

        Role passengerRole = roleRepo.findByRoleName("PASSENGER").orElseGet(() -> {
            Role newRole = Role.builder().roleName("PASSENGER").build();
            return roleRepo.save(newRole);
        });

        Optional<User> adminUserOptional = userRepo.findByEmail("admin@thy.com");
        if (adminUserOptional.isEmpty()) {
            User adminUser = User.builder()
                    .fullName("Admin User")
                    .email("admin@thy.com")
                    .passwordHash("admin123")
                    .phoneNumber("5551234567")
                    .registrationDate(LocalDateTime.now())
                    .build();
            userRepo.save(adminUser);

            UserRole adminUserRole = UserRole.builder().user(adminUser).role(adminRole).build();
            userRoleRepo.save(adminUserRole);
        }

        Optional<User> staffUserOptional = userRepo.findByEmail("staff@thy.com");
        if (staffUserOptional.isEmpty()) {
            User staffUser = User.builder()
                    .fullName("Staff Member")
                    .email("staff@thy.com")
                    .passwordHash("staff123")
                    .phoneNumber("5557654321")
                    .registrationDate(LocalDateTime.now())
                    .build();
            userRepo.save(staffUser);

            UserRole staffUserRole = UserRole.builder().user(staffUser).role(staffRole).build();
            userRoleRepo.save(staffUserRole);
        }

        userRepo.findAll().forEach(user -> {
            boolean isPassenger = userRoleRepo.findByUserAndRole_RoleName(user, "PASSENGER").isPresent();
            if (isPassenger) {
                boolean passengerEntryExists = passengerRepo.findByUser(user).isPresent();
                if (!passengerEntryExists) {
                    Passenger passenger = Passenger.builder()
                            .user(user)
                            .nationality(null)
                            .passportNumber(null)
                            .build();
                    passengerRepo.save(passenger);
                }
            }
        });
    }
}