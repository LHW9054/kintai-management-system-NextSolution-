package com.nextsolution.kintai.dto;

public class MonthlySummaryDto {

    private Long userId;
    private String yearMonth;
    private Double totalWorkHours = 0.0;
    private Double totalOvertime = 0.0;
    private Double totalNightHours = 0.0;
    private Integer lateCount = 0;
    private Integer leaveDays = 0;
    private Integer workDays = 0;

    // Getter & Setter
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }

    public Double getTotalWorkHours() { return totalWorkHours; }
    public void setTotalWorkHours(Double totalWorkHours) { this.totalWorkHours = totalWorkHours; }

    public Double getTotalOvertime() { return totalOvertime; }
    public void setTotalOvertime(Double totalOvertime) { this.totalOvertime = totalOvertime; }

    public Integer getLateCount() { return lateCount; }
    public void setLateCount(Integer lateCount) { this.lateCount = lateCount; }

    public Integer getLeaveDays() { return leaveDays; }
    public void setLeaveDays(Integer leaveDays) { this.leaveDays = leaveDays; }

    public Double getTotalNightHours() { return totalNightHours; }
    public void setTotalNightHours(Double totalNightHours) { this.totalNightHours = totalNightHours; }

    public Integer getWorkDays() { return workDays; }
    public void setWorkDays(Integer workDays) { this.workDays = workDays; }
}