package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(String roleName);
}
