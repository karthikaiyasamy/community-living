package com.clbc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    @Qualifier("auditJdbcTemplate")
    private final JdbcTemplate auditJdbcTemplate;

    public void logAction(String action, String resourceType, String resourceId, String details, String ipAddress) {
        String userId = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "SYSTEM";

        String sql = "INSERT INTO audit_logs (user_id, action, resource_type, resource_id, details, ip_address) VALUES (?, ?, ?, ?, ?, ?)";
        auditJdbcTemplate.update(sql, userId, action, resourceType, resourceId, details, ipAddress);
    }

    public void logLogin(String userId, boolean success, String ipAddress, String failureReason) {
        String sql = "INSERT INTO login_logs (user_id, success, ip_address, failure_reason) VALUES (?, ?, ?, ?)";
        auditJdbcTemplate.update(sql, userId, success, ipAddress, failureReason);
    }
}
