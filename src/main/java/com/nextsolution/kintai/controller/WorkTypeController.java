package com.nextsolution.kintai.controller;

import com.nextsolution.kintai.entity.WorkType;
import com.nextsolution.kintai.service.WorkTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

// システム設定：勤務形態（勤務時間）管理（システム管理者専用）
@RestController
@RequestMapping("/api/worktypes")
public class WorkTypeController {

    @Autowired
    private WorkTypeService workTypeService;

    @GetMapping
    public List<WorkType> getAllWorkTypes() {
        return workTypeService.getAllWorkTypes();
    }

    @PostMapping
    public WorkType createWorkType(
            @RequestParam String typeName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam BigDecimal breakTimeHours,
            @RequestParam BigDecimal requiredHours) {
        return workTypeService.createWorkType(typeName, startTime, endTime, breakTimeHours, requiredHours);
    }

    @PutMapping("/{workTypeId}")
    public WorkType updateWorkType(
            @PathVariable Long workTypeId,
            @RequestParam String typeName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam BigDecimal breakTimeHours,
            @RequestParam BigDecimal requiredHours) {
        return workTypeService.updateWorkType(workTypeId, typeName, startTime, endTime, breakTimeHours, requiredHours);
    }

    @DeleteMapping("/{workTypeId}")
    public String deleteWorkType(@PathVariable Long workTypeId) {
        workTypeService.deleteWorkType(workTypeId);
        return "勤務形態を削除しました.";
    }
}
