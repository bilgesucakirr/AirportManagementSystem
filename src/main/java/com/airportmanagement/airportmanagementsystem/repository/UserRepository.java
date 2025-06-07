package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.User;
import com.airportmanagement.airportmanagementsystem.dto.UserWithRoleDTO;
import com.airportmanagement.airportmanagementsystem.dto.PassengerProfileDTO; // Yeni import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query(value = "EXEC sp_GetAllUsersWithRoles", nativeQuery = true)
    List<UserWithRoleDTO> findAllUsersWithRoles();

    @Procedure(procedureName = "sp_AddUserAndAssignRole")
    Integer addAndAssignRole(@Param("FullName") String fullName,
                             @Param("Email") String email,
                             @Param("PasswordHash") String passwordHash,
                             @Param("PhoneNumber") String phoneNumber,
                             @Param("RoleName") String roleName);
    @Procedure(procedureName = "sp_UpdateUserDetails")
    Integer updateUserDetails(@Param("UserID") Integer userID,
                              @Param("FullName") String fullName,
                              @Param("Email") String email,
                              @Param("PhoneNumber") String phoneNumber);

    @Procedure(procedureName = "sp_RemoveUserRole")
    Integer removeUserRole(@Param("UserID") Integer userID,
                           @Param("RoleName") String roleName);

    @Procedure(procedureName = "sp_DeleteUserCompletely")
    Integer deleteUserCompletely(@Param("UserID") Integer userID);


    @Query(value = "EXEC sp_GetPassengerProfile @UserID = :userID", nativeQuery = true)
    Optional<PassengerProfileDTO> getPassengerProfile(@Param("userID") Integer userID);

    @Procedure(procedureName = "sp_UpdatePassengerProfile")
    Integer updatePassengerProfile(@Param("UserID") Integer userID,
                                   @Param("FullName") String fullName,
                                   @Param("Email") String email,
                                   @Param("PasswordHash") String passwordHash,
                                   @Param("PhoneNumber") String phoneNumber,
                                   @Param("Nationality") String nationality,
                                   @Param("PassportNumber") String passportNumber);
}