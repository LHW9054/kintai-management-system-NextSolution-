package com.nextsolution.kintai.repository;

import com.nextsolution.kintai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 社員番号でユーザーを検索
    Optional<User> findByEmployeeCode(String employeeCode);

    long countByRole(String role);
}