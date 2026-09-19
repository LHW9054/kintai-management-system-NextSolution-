package com.nextsolution.kintai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_edit_request")
public class WorkEditRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long editRequestId;

    private Long userId;
    private Long attendanceId;
    private LocalDateTime requestedClockIn;
    private LocalDateTime requestedClockOut;
    private String reason;
    private String status; // PENDING, APPROVED, REJECTED

    // Getter & Setter
    public Long getEditRequestId() { return editRequestId; }
    public void setEditRequestId(Long editRequestId) { this.editRequestId = editRequestId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }

    public LocalDateTime getRequestedClockIn() { return requestedClockIn; }
    public void setRequestedClockIn(LocalDateTime requestedClockIn) { this.requestedClockIn = requestedClockIn; }

    public LocalDateTime getRequestedClockOut() { return requestedClockOut; }
    public void setRequestedClockOut(LocalDateTime requestedClockOut) { this.requestedClockOut = requestedClockOut; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}