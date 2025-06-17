package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @Query(value = "EXEC sp_GetNotificationsForUser @UserID = :userID", nativeQuery = true)
    List<Notification> getNotificationsForUser(@Param("userID") Integer userID);

    @Procedure(procedureName = "sp_MarkNotificationAsRead")
    Integer markNotificationAsRead(@Param("NotificationID") Integer notificationID);

    long countByUser_UserIDAndIsReadFalse(Integer userID);
}