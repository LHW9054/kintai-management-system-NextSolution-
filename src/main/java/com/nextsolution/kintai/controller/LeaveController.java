package com.nextsolution.kintai.controller;

import com.nextsolution.kintai.entity.LeaveRequest;
import com.nextsolution.kintai.service.LeaveService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {
    private final LeaveService service;
    public LeaveController(LeaveService service){this.service=service;}

    @PostMapping("/apply")
    public LeaveRequest apply(@RequestParam Long userId,@RequestParam String leaveType,
                              @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate startDate,
                              @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate endDate,
                              @RequestParam String reason){return service.applyLeave(userId,leaveType,startDate,endDate,reason);}
    @GetMapping("/pending") public List<LeaveRequest> pending(@RequestParam(required=false) Long managerId){return service.getPendingRequests(managerId);}
    @GetMapping("/my-pending") public List<LeaveRequest> myPending(@RequestParam Long userId){return service.getMyPending(userId);}
    @GetMapping("/balance") public Map<String,Object> balance(@RequestParam Long userId){return service.getBalance(userId);}
    @PostMapping("/approve") public LeaveRequest approve(@RequestParam Long leaveId,@RequestParam String status,@RequestParam(required=false) Long approverId){return service.processApproval(leaveId,status,approverId);}
}
