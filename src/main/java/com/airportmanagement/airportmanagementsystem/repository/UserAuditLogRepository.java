package com.airportmanagement.airportmanagementsystem.repository;

import com.airportmanagement.airportmanagementsystem.entity.UserAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserAuditLogRepository extends JpaRepository<UserAuditLog, Integer> {
    List<UserAuditLog> findByUserIDOrderByChangeTimestampDesc(Integer userID);

}