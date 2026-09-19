package com.nextsolution.kintai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 操作ログ一覧取得API
    @GetMapping("/logs")
    public List<Map<String, Object>> getAuditLogs() {
        String sql = "SELECT * FROM audit_log ORDER BY created_at DESC LIMIT 50";
        return jdbcTemplate.queryForList(sql);
    }
}