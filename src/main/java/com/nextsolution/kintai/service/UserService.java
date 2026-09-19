package com.nextsolution.kintai.service;

import com.nextsolution.kintai.entity.User;
import com.nextsolution.kintai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ログイン検証処理
    public boolean login(String employeeCode, String password) {
        User user = userRepository.findByEmployeeCode(employeeCode).orElse(null);
        if (user != null && user.getPassword() != null && passwordEncoder.matches(password, user.getPassword())) {
            return true;
        }
        return false;
    }

    // ユーザー管理：部署名・上司名・勤務形態名を結合して一覧取得
    public List<Map<String, Object>> getAllUsersWithDetail() {
        String sql = "SELECT u.user_id, u.employee_code, u.name, u.role, " +
                "u.dept_id, d.dept_name, u.manager_id, m.name AS manager_name, " +
                "u.work_type_id, w.type_name AS work_type_name, u.created_at " +
                "FROM users u " +
                "LEFT JOIN department d ON u.dept_id = d.dept_id " +
                "LEFT JOIN users m ON u.manager_id = m.user_id " +
                "LEFT JOIN work_type w ON u.work_type_id = w.work_type_id " +
                "ORDER BY u.user_id";
        return jdbcTemplate.queryForList(sql);
    }

    // 上司指定用候補一覧（上司・人事・管理者権限）
    public List<Map<String, Object>> getManagerCandidates() {
        String sql = "SELECT user_id, employee_code, name, role FROM users " +
                "WHERE role IN ('MANAGER', 'HR', 'ADMIN') ORDER BY name";
        return jdbcTemplate.queryForList(sql);
    }

    public User createUser(String employeeCode, String name, String password, Long deptId,
            Long managerId, String role, Long workTypeId) {
        if ("ADMIN".equals(role) && userRepository.countByRole("ADMIN") >= 1)
            throw new IllegalArgumentException("システム管理者は一名のみ登録できます.");
        User user = new User();
        user.setEmployeeCode(employeeCode);
        user.setName(name);
        validatePassword(password);
        user.setPassword(passwordEncoder.encode(password));
        user.setDeptId(deptId);
        user.setManagerId(managerId);
        user.setRole(role);
        user.setWorkTypeId(workTypeId);
        return userRepository.save(user);
    }

    public User updateUser(Long userId, String name, String password, Long deptId,
            Long managerId, String role, Long workTypeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが存在しません."));
        user.setName(name);
        if ("ADMIN".equals(role) && !"ADMIN".equals(user.getRole()) && userRepository.countByRole("ADMIN") >= 1)
            throw new IllegalArgumentException("システム管理者は一名のみ登録できます.");
        if (password != null && !password.isBlank()) {
            validatePassword(password);
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setDeptId(deptId);
        user.setManagerId(managerId);
        user.setRole(role);
        user.setWorkTypeId(workTypeId);
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8 || !password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("パスワードは8文字以上で、英大文字・小文字を含めてください.");
        }
    }

}
