package com.nextsolution.kintai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // システム操作ログ記録（5年間保存対象）
    public void logAction(Long userId, String action, String ipAddress, String details) {
        String sql = "INSERT INTO audit_log (user_id, action, ip_address, details) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, action, ipAddress, details);
    }
}