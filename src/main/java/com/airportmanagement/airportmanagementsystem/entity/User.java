package com.airportmanagement.airportmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@NamedNativeQueries({
        @NamedNativeQuery(
                name = "User.findAllUsersWithRoles",
                query = "EXEC sp_GetAllUsersWithRoles",
                resultSetMapping = "UserWithRoleDTOMapping"
        )
})
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "UserWithRoleDTOMapping",
                classes = @ConstructorResult(
                        targetClass = com.airportmanagement.airportmanagementsystem.dto.UserWithRoleDTO.class,
                        columns = {
                                @ColumnResult(name = "UserID", type = Integer.class),
                                @ColumnResult(name = "FullName", type = String.class),
                                @ColumnResult(name = "Email", type = String.class),
                                @ColumnResult(name = "PhoneNumber", type = String.class),
                                @ColumnResult(name = "RegistrationDate", type = LocalDateTime.class), // Burada tip belirtiyoruz
                                @ColumnResult(name = "Roles", type = String.class)
                        }
                )
        )
})

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userID;

    private String fullName;
    private String email;
    private String passwordHash;
    private String phoneNumber;

    private LocalDateTime registrationDate = LocalDateTime.now();
}