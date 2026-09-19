package com.nextsolution.kintai.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nextsolution.kintai.service.DevClock;

import java.util.List;
import java.util.Map;

/** 上司が直属部下の勤怠・休暇状況を確認するAPI。 */
@RestController
@RequestMapping("/api/manager")
public class ManagerController {
    private final JdbcTemplate jdbc;
    private final DevClock clock;

    public ManagerController(JdbcTemplate jdbc, DevClock clock) { this.jdbc = jdbc; this.clock = clock; }

    /** 直属部下の基本情報と本日の打刻状況を返す。 */
    @GetMapping("/team-attendance")
    public List<Map<String,Object>> teamAttendance(@RequestParam Long managerId) {
        String sql = "SELECT u.user_id,u.employee_code,u.name,d.dept_name," +
                "a.work_date,a.clock_in,a.break_start,a.break_end,a.clock_out,a.total_work_hours " +
                "FROM users u LEFT JOIN department d ON u.dept_id=d.dept_id " +
                "LEFT JOIN attendance a ON a.user_id=u.user_id AND a.work_date=? " +
                "WHERE u.manager_id=? ORDER BY u.employee_code";
        return jdbc.queryForList(sql, clock.today(), managerId);
    }

    /** 直属部下の休暇申請を新しい順に返す。 */
    @GetMapping("/team-leave")
    public List<Map<String,Object>> teamLeave(@RequestParam Long managerId) {
        String sql = "SELECT lr.leave_request_id,u.employee_code,u.name,lr.leave_type," +
                "lr.start_date,lr.end_date,lr.days_count,lr.reason,lr.status " +
                "FROM leave_request lr JOIN users u ON lr.user_id=u.user_id " +
                "WHERE u.manager_id=? ORDER BY lr.start_date DESC";
        return jdbc.queryForList(sql, managerId);
    }
}
