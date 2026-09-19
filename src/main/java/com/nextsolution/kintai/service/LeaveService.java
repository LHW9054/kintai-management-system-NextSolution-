package com.nextsolution.kintai.service;

import com.nextsolution.kintai.entity.LeaveRequest;
import com.nextsolution.kintai.entity.User;
import com.nextsolution.kintai.repository.LeaveRequestRepository;
import com.nextsolution.kintai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.*;
import java.util.*;

@Service
public class LeaveService {
    private final LeaveRequestRepository repo;
    private final UserRepository users;
    private final DevClock clock;
    private final JdbcTemplate jdbc;

    public LeaveService(LeaveRequestRepository repo, UserRepository users, DevClock clock, JdbcTemplate jdbc) {
        this.repo = repo;
        this.users = users;
        this.clock = clock;
        this.jdbc = jdbc;
    }

    public LeaveRequest applyLeave(Long userId, String leaveType, LocalDate startDate, LocalDate endDate,
            String reason) {
        if (startDate == null || endDate == null)
            throw new IllegalArgumentException("休暇日を入力してください.");
        if (endDate.isBefore(startDate))
            throw new IllegalArgumentException("終了日は開始日より前に設定できません.");
        if (clock.now().isAfter(startDate.minusDays(1).atTime(18, 0)))
            throw new IllegalArgumentException("休暇申請は開始日前日の18時まで可能です.");
        long days = Duration.between(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()).toDays();
        User u = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("ユーザーが存在しません."));
        double used = repo.findByUserIdAndStatus(userId, "APPROVED").stream()
                .filter(x -> "PAID".equals(x.getLeaveType())).mapToDouble(x -> n(x.getDaysCount())).sum();
        if ("PAID".equals(leaveType) && used + days > n(u.getAnnualPaidLeave()))
            throw new IllegalArgumentException("有給休暇の残日数を超えています.");
        LeaveRequest r = new LeaveRequest();
        r.setUserId(userId);
        r.setLeaveType(leaveType);
        r.setStartDate(startDate);
        r.setEndDate(endDate);
        r.setDaysCount((double) days);
        r.setReason(reason);
        r.setStatus("PENDING");
        LeaveRequest saved = repo.save(r);
        System.out.printf("[%s] [休暇申請] %s（ユーザーID:%d）が%sから%sまで休暇を申請しました。%n", clock.now(), u.getName(), userId, startDate,
                endDate);
        return saved;
    }

    public List<LeaveRequest> getPendingRequests(Long managerId) {
        return managerId == null ? repo.findByStatus("PENDING") : repo.findPendingByManager("PENDING", managerId);
    }

    public List<LeaveRequest> getMyPending(Long userId) {
        return repo.findByUserIdAndStatus(userId, "PENDING");
    }

    public Map<String, Object> getBalance(Long userId) {
        User u = users.findById(userId).orElseThrow();
        double annual = n(u.getAnnualPaidLeave());
        double used = repo.findByUserIdAndStatus(userId, "APPROVED").stream()
                .filter(x -> "PAID".equals(x.getLeaveType())).mapToDouble(x -> n(x.getDaysCount())).sum();
        double pending = repo.findByUserIdAndStatus(userId, "PENDING").stream()
                .filter(x -> "PAID".equals(x.getLeaveType())).mapToDouble(x -> n(x.getDaysCount())).sum();
        Map<String, Object> m = new HashMap<>();
        m.put("annual", annual);
        m.put("used", used);
        m.put("remaining", Math.max(0, annual - used));
        m.put("pending", pending);
        return m;
    }

    public LeaveRequest processApproval(Long id, String status, Long approverId) {
        if (!Set.of("APPROVED", "REJECTED").contains(status))
            throw new IllegalArgumentException("承認状態が正しくありません.");
        LeaveRequest r = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("申請が存在しません。"));
        r.setStatus(status);
        LeaveRequest saved = repo.save(r);
        jdbc.update(
                "INSERT INTO approval_history(request_type,request_id,requester_id,approver_id,status,comment) VALUES(?,?,?,?,?,?)",
                "LEAVE", id, r.getUserId(), approverId, status, "休暇承認処理");
        String employeeName = users.findById(r.getUserId()).map(User::getName).orElse("不明");
        String approverName = approverId == null ? "不明" : users.findById(approverId).map(User::getName).orElse("不明");
        System.out.printf("[%s] [休暇承認] %sが%sの休暇申請を%sにしました。%n", clock.now(), approverName, employeeName, status);
        return saved;
    }

    private double n(Double d) {
        return d == null ? 0 : d;
    }
}
