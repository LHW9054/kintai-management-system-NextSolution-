package com.nextsolution.kintai.repository;

import com.nextsolution.kintai.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUserId(Long userId);
    List<LeaveRequest> findByStatus(String status);
    List<LeaveRequest> findByUserIdAndStatus(Long userId, String status);

    @Query(value = "SELECT lr.* FROM leave_request lr JOIN users u ON lr.user_id = u.user_id WHERE lr.status = :status AND u.manager_id = :managerId ORDER BY lr.start_date", nativeQuery = true)
    List<LeaveRequest> findPendingByManager(@Param("status") String status, @Param("managerId") Long managerId);

    // 期間内の承認済み休暇取得（月次集計用）
    List<LeaveRequest> findByUserIdAndStatusAndStartDateBetween(Long userId, String status, LocalDate startDate, LocalDate endDate);
}
