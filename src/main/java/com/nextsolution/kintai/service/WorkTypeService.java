package com.nextsolution.kintai.service;

import com.nextsolution.kintai.entity.WorkType;
import com.nextsolution.kintai.repository.WorkTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Service
public class WorkTypeService {

    @Autowired
    private WorkTypeRepository workTypeRepository;

    public List<WorkType> getAllWorkTypes() {
        return workTypeRepository.findAll();
    }

    public WorkType createWorkType(String typeName, LocalTime startTime, LocalTime endTime,
            BigDecimal breakTimeHours, BigDecimal requiredHours) {
        WorkType wt = new WorkType();
        wt.setTypeName(typeName);
        wt.setStartTime(startTime);
        wt.setEndTime(endTime);
        wt.setBreakTimeHours(breakTimeHours);
        wt.setRequiredHours(requiredHours);
        return workTypeRepository.save(wt);
    }

    public WorkType updateWorkType(Long workTypeId, String typeName, LocalTime startTime, LocalTime endTime,
            BigDecimal breakTimeHours, BigDecimal requiredHours) {
        WorkType wt = workTypeRepository.findById(workTypeId)
                .orElseThrow(() -> new IllegalArgumentException("勤務形態が存在しません."));
        wt.setTypeName(typeName);
        wt.setStartTime(startTime);
        wt.setEndTime(endTime);
        wt.setBreakTimeHours(breakTimeHours);
        wt.setRequiredHours(requiredHours);
        return workTypeRepository.save(wt);
    }

    public void deleteWorkType(Long workTypeId) {
        workTypeRepository.deleteById(workTypeId);
    }
}
