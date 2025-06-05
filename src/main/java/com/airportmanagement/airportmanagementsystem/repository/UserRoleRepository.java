package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    Optional<UserRole> findByUserAndRole_RoleName(User user, String roleName);
    long countByRole_RoleName(String roleName);
}