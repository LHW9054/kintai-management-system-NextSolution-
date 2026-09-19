package com.nextsolution.kintai.repository;

import com.nextsolution.kintai.entity.WorkEditRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkEditRequestRepository extends JpaRepository<WorkEditRequest, Long> {
    List<WorkEditRequest> findByStatus(String status);
}