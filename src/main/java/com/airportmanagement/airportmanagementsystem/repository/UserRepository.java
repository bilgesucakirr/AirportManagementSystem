package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.dto.UserWithRoleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    List<UserWithRoleDTO> findAllUsersWithRoles();

    @Transactional
    @Modifying
    @Procedure(procedureName = "sp_AddUserAndAssignRole", outputParameterName = "NewUserID")
    Integer addUserAndAssignRole(
            @Param("FullName") String fullName,
            @Param("Email") String email,
            @Param("PasswordHash") String passwordHash,
            @Param("PhoneNumber") String phoneNumber,
            @Param("RoleName") String roleName
    );

    @Transactional
    @Modifying
    @Procedure(procedureName = "sp_UpdateUserDetails", outputParameterName = "ResultCode")
    Integer updateUserDetails(
            @Param("UserID") Integer userID,
            @Param("FullName") String fullName,
            @Param("Email") String email,
            @Param("PhoneNumber") String phoneNumber
    );

    @Transactional
    @Modifying
    @Procedure(procedureName = "sp_RemoveUserRole", outputParameterName = "ResultCode")
    Integer removeUserRole(
            @Param("UserID") Integer userID,
            @Param("RoleName") String roleName
    );

    @Transactional
    @Modifying
    @Procedure(procedureName = "sp_DeleteUserCompletely", outputParameterName = "ResultCode")
    Integer deleteUserCompletely(
            @Param("UserID") Integer userID
    );
}