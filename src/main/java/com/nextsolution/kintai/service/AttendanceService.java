package com.nextsolution.kintai.service;

import com.nextsolution.kintai.dto.MonthlySummaryDto;
import com.nextsolution.kintai.entity.Attendance;
import com.nextsolution.kintai.entity.User;
import com.nextsolution.kintai.entity.WorkType;
import com.nextsolution.kintai.repository.AttendanceRepository;
import com.nextsolution.kintai.repository.LeaveRequestRepository;
import com.nextsolution.kintai.repository.UserRepository;
import com.nextsolution.kintai.repository.WorkEditRequestRepository;
import com.nextsolution.kintai.repository.WorkTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final WorkEditRequestRepository editRequestRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;
    private final WorkTypeRepository workTypeRepository;
    private final DevClock clock;
    private final JdbcTemplate jdbc;

    public AttendanceService(AttendanceRepository attendanceRepository,
            WorkEditRequestRepository editRequestRepository,
            LeaveRequestRepository leaveRequestRepository,
            UserRepository userRepository,
            WorkTypeRepository workTypeRepository,
            DevClock clock, JdbcTemplate jdbc) {
        this.attendanceRepository = attendanceRepository;
        this.editRequestRepository = editRequestRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.userRepository = userRepository;
        this.workTypeRepository = workTypeRepository;
        this.clock = clock;
        this.jdbc = jdbc;
    }

    private Attendance todayRecord(Long userId) {
        return attendanceRepository.findByUserIdAndWorkDate(userId, clock.today()).orElse(null);
    }

    private WorkType workType(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません。"));
        return user.getWorkTypeId() == null
                ? workTypeRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("勤務形態が登録されていません。"))
                : workTypeRepository.findById(user.getWorkTypeId())
                        .orElseThrow(() -> new IllegalArgumentException("勤務形態が登録されていません。"));
    }

    /** 利用者名を含む操作内容を発表時にも分かりやすい形式で端末へ出力する。 */
    private void terminalLog(Long userId, String action, LocalDateTime time) {
        String name = userRepository.findById(userId).map(User::getName).orElse("不明なユーザー");
        System.out.printf("[%s] [勤怠操作] %s（ユーザーID:%d）が%sしました。%n", time, name, userId, action);
    }

    public Attendance clockIn(Long userId) {
        LocalDateTime now = clock.now();
        Attendance existing = todayRecord(userId);
        if (existing != null && existing.getClockIn() != null)
            throw new IllegalStateException("すでに出勤打刻済みです。");

        Attendance a = existing != null ? existing : new Attendance();
        a.setUserId(userId);
        a.setWorkDate(now.toLocalDate());
        a.setClockIn(now);
        a.setClockOut(null);
        a.setBreakStart(null);
        a.setBreakEnd(null);
        a.setTotalBreakHours(0.0);
        a.setIsLate(now.isAfter(LocalDateTime.of(now.toLocalDate(), workType(userId).getStartTime()).plusMinutes(1)));
        a.setIsFulfilled(false);
        Attendance saved = attendanceRepository.save(a);
        terminalLog(userId, "出勤", now);
        return saved;
    }

    public Attendance clockOut(Long userId) {
        Attendance a = todayRecord(userId);
        if (a == null || a.getClockIn() == null)
            throw new IllegalStateException("出勤記録がないため退勤できません。");
        if (a.getClockOut() != null)
            throw new IllegalStateException("すでに退勤打刻済みです。");
        if (a.getBreakStart() != null && a.getBreakEnd() == null)
            throw new IllegalStateException("休憩中です。先に休憩終了を行ってください。");
        LocalDateTime now = clock.now();
        a.setClockOut(now);
        recalculate(a, workType(userId));
        Attendance saved = attendanceRepository.save(a);
        terminalLog(userId, "退勤", now);
        return saved;
    }

    @Transactional
    public Attendance restStart(Long userId) {
        Attendance a = todayRecord(userId);
        if (a == null || a.getClockIn() == null)
            throw new IllegalStateException("出勤後に休憩を開始できます。");
        if (a.getClockOut() != null)
            throw new IllegalStateException("退勤後は休憩を開始できません。");
        if (a.getBreakStart() != null && a.getBreakEnd() == null)
            throw new IllegalStateException("すでに休憩中です。");
        a.setBreakStart(clock.now());
        a.setBreakEnd(null);
        Attendance saved = attendanceRepository.save(a);
        terminalLog(userId, "休憩開始", a.getBreakStart());
        return saved;
    }

    @Transactional
    public Attendance restEnd(Long userId) {
        Attendance a = todayRecord(userId);
        if (a == null || a.getClockIn() == null)
            throw new IllegalStateException("出勤後に休憩終了できます。");
        if (a.getClockOut() != null)
            throw new IllegalStateException("退勤後は休憩終了できません。");
        if (a.getBreakStart() == null)
            throw new IllegalStateException("開始中の休憩がありません。");
        if (a.getBreakEnd() != null)
            throw new IllegalStateException("すでに休憩終了済みです。");
        a.setBreakEnd(clock.now());
        long minutes = Math.max(0, Duration.between(a.getBreakStart(), a.getBreakEnd()).toMinutes());
        a.setTotalBreakHours(round2(minutes / 60.0));
        Attendance saved = attendanceRepository.save(a);
        terminalLog(userId, "休憩終了", a.getBreakEnd());
        return saved;
    }

    private void recalculate(Attendance a, WorkType wt) {
        LocalDateTime in = a.getClockIn(), out = a.getClockOut();
        long totalMinutes = Math.max(0, Duration.between(in, out).toMinutes());
        long breakMinutes = Math.round((a.getTotalBreakHours() == null ? 0 : a.getTotalBreakHours()) * 60);
        long workMinutes = Math.max(0, totalMinutes - breakMinutes);
        a.setTotalWorkHours(round2(workMinutes / 60.0));
        a.setIsFulfilled(a.getTotalWorkHours() >= wt.getRequiredHours().doubleValue());
        a.setOvertimeHours(round2(overlapMinutes(in, out, LocalDateTime.of(a.getWorkDate(), wt.getEndTime())) / 60.0));
        a.setNightHours(round2(overlapMinutes(in, out, LocalDateTime.of(a.getWorkDate(), LocalTime.of(22, 0))) / 60.0));
    }

    private long overlapMinutes(LocalDateTime start, LocalDateTime end, LocalDateTime threshold) {
        if (!end.isAfter(threshold))
            return 0;
        long minutes = Duration.between(start.isAfter(threshold) ? start : threshold, end).toMinutes();
        long breakMinutes = Math.round((todayRecord(0L) == null ? 0 : 0) * 60); // break is already represented in total
                                                                                // work; overtime follows clock span per
                                                                                // rule
        return Math.max(0, minutes - breakMinutes);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public Map<String, Object> getTodayStatus(Long userId) {
        Attendance a = todayRecord(userId);
        double work = a == null || a.getTotalWorkHours() == null ? 0 : a.getTotalWorkHours();
        if (a != null && a.getClockIn() != null && a.getClockOut() == null && a.getBreakStart() == null) {
            work = round2(Duration.between(a.getClockIn(), clock.now()).toMinutes() / 60.0
                    - (a.getTotalBreakHours() == null ? 0 : a.getTotalBreakHours()));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("date", clock.today());
        result.put("workHours", Math.max(0, work));
        result.put("clockIn", a == null ? null : a.getClockIn());
        result.put("clockOut", a == null ? null : a.getClockOut());
        result.put("breakStart", a == null ? null : a.getBreakStart());
        result.put("breakEnd", a == null ? null : a.getBreakEnd());
        result.put("fulfilled", a != null && Boolean.TRUE.equals(a.getIsFulfilled()));
        return result;
    }

    public MonthlySummaryDto getMonthlySummary(Long userId, String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        List<Attendance> records = attendanceRepository.findByUserIdAndWorkDateBetween(userId, ym.atDay(1),
                ym.atEndOfMonth());
        double totalWork = 0, totalOvertime = 0, totalNight = 0;
        int late = 0, days = 0;
        for (Attendance a : records) {
            if (a.getClockIn() != null)
                days++;
            totalWork += n(a.getTotalWorkHours());
            totalOvertime += n(a.getOvertimeHours());
            totalNight += n(a.getNightHours());
            if (Boolean.TRUE.equals(a.getIsLate()))
                late++;
        }
        double leaveDays = leaveRequestRepository
                .findByUserIdAndStatusAndStartDateBetween(userId, "APPROVED", ym.atDay(1), ym.atEndOfMonth())
                .stream().mapToDouble(l -> n(l.getDaysCount())).sum();
        MonthlySummaryDto s = new MonthlySummaryDto();
        s.setUserId(userId);
        s.setYearMonth(yearMonth);
        s.setTotalWorkHours(round2(totalWork));
        s.setTotalOvertime(round2(totalOvertime));
        s.setTotalNightHours(round2(totalNight));
        s.setLateCount(late);
        s.setWorkDays(days);
        s.setLeaveDays((int) Math.round(leaveDays));
        return s;
    }

    private double n(Double d) {
        return d == null ? 0 : d;
    }

    public com.nextsolution.kintai.entity.WorkEditRequest requestWorkEdit(Long userId, Long attendanceId,
            LocalDateTime clockIn, LocalDateTime clockOut, String reason) {
        if (clockIn == null || clockOut == null || !clockOut.isAfter(clockIn))
            throw new IllegalArgumentException("修正時間の範囲が正しくありません。");
        var r = new com.nextsolution.kintai.entity.WorkEditRequest();
        r.setUserId(userId);
        r.setAttendanceId(attendanceId);
        r.setRequestedClockIn(clockIn);
        r.setRequestedClockOut(clockOut);
        r.setReason(reason);
        r.setStatus("PENDING");
        return editRequestRepository.save(r);
    }

    @Transactional
    public void approveWorkEdit(Long id, String status, Long approverId) {
        var r = editRequestRepository.findById(id).orElseThrow();
        r.setStatus(status);
        if ("APPROVED".equals(status)) {
            Attendance a = attendanceRepository.findById(r.getAttendanceId()).orElseThrow();
            a.setClockIn(r.getRequestedClockIn());
            a.setClockOut(r.getRequestedClockOut());
            recalculate(a, workType(a.getUserId()));
            attendanceRepository.save(a);
        }
        jdbc.update(
                "INSERT INTO approval_history(request_type,request_id,requester_id,approver_id,status,comment) VALUES(?,?,?,?,?,?)",
                "WORK_EDIT", id, r.getUserId(), approverId, status, "勤務修正の承認処理");
    }

    public List<com.nextsolution.kintai.entity.WorkEditRequest> getPendingEditRequests(Long managerId) {
        return editRequestRepository.findByStatus("PENDING");
    }
}
