package com.nextsolution.kintai.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "work_type")
public class WorkType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workTypeId;

    @Column(nullable = false)
    private String typeName;

    @Column(nullable = false)
    private LocalTime startTime = LocalTime.of(9, 0, 0);

    @Column(nullable = false)
    private LocalTime endTime = LocalTime.of(18, 0, 0);

    private BigDecimal breakTimeHours = BigDecimal.valueOf(1.0);
    private BigDecimal requiredHours = BigDecimal.valueOf(8.0);

    public Long getWorkTypeId() {
        return workTypeId;
    }

    public void setWorkTypeId(Long workTypeId) {
        this.workTypeId = workTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getBreakTimeHours() {
        return breakTimeHours;
    }

    public void setBreakTimeHours(BigDecimal breakTimeHours) {
        this.breakTimeHours = breakTimeHours;
    }

    public BigDecimal getRequiredHours() {
        return requiredHours;
    }

    public void setRequiredHours(BigDecimal requiredHours) {
        this.requiredHours = requiredHours;
    }
}
