package com.nextsolution.kintai.controller;

import com.nextsolution.kintai.dto.MonthlySummaryDto;
import com.nextsolution.kintai.entity.Attendance;
import com.nextsolution.kintai.entity.WorkEditRequest;
import com.nextsolution.kintai.service.AttendanceService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService service;
    public AttendanceController(AttendanceService service){this.service=service;}

    @PostMapping("/clock-in") public Attendance clockIn(@RequestParam Long userId){return service.clockIn(userId);}
    @PostMapping("/clock-out") public Attendance clockOut(@RequestParam Long userId){return service.clockOut(userId);}
    @PostMapping("/rest-start") public Attendance restStart(@RequestParam Long userId){return service.restStart(userId);}
    @PostMapping("/rest-end") public Attendance restEnd(@RequestParam Long userId){return service.restEnd(userId);}
    @GetMapping("/today") public Map<String,Object> today(@RequestParam Long userId){return service.getTodayStatus(userId);}
    @GetMapping("/monthly-summary") public MonthlySummaryDto monthly(@RequestParam Long userId,@RequestParam String yearMonth){return service.getMonthlySummary(userId,yearMonth);}

    @PostMapping("/edit-request")
    public WorkEditRequest edit(@RequestParam Long userId,@RequestParam Long attendanceId,@RequestParam String clockIn,@RequestParam String clockOut,@RequestParam String reason){
        return service.requestWorkEdit(userId,attendanceId,LocalDateTime.parse(clockIn),LocalDateTime.parse(clockOut),reason);
    }
    @GetMapping("/edit-pending") public List<WorkEditRequest> pending(@RequestParam(required=false) Long managerId){return service.getPendingEditRequests(managerId);}
    @PostMapping("/approve-edit") public String approve(@RequestParam Long editRequestId,@RequestParam String status,@RequestParam(required=false) Long approverId){service.approveWorkEdit(editRequestId,status,approverId);return "処理が完了しました.";}
}
