package com.nextsolution.kintai.repository;

import com.nextsolution.kintai.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 指定社員・指定日の出退勤記録取得
    Optional<Attendance> findByUserIdAndWorkDate(Long userId, LocalDate workDate);

    // 指定社員の期間内出退勤記録取得（月次集計用）
    List<Attendance> findByUserIdAndWorkDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
