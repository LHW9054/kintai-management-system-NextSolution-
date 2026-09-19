package com.nextsolution.kintai.controller;

import com.nextsolution.kintai.entity.User;
import com.nextsolution.kintai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// ユーザー管理（システム管理者・人事部専用）
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<Map<String, Object>> getAllUsers() {
        return userService.getAllUsersWithDetail();
    }

    @GetMapping("/managers")
    public List<Map<String, Object>> getManagerCandidates() {
        return userService.getManagerCandidates();
    }

    @PostMapping
    public User createUser(
            @RequestParam String employeeCode,
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long managerId,
            @RequestParam String role,
            @RequestParam(required = false) Long workTypeId) {
        return userService.createUser(employeeCode, name, password, deptId, managerId, role, workTypeId);
    }

    @PutMapping("/{userId}")
    public User updateUser(
            @PathVariable Long userId,
            @RequestParam String name,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long managerId,
            @RequestParam String role,
            @RequestParam(required = false) Long workTypeId) {
        return userService.updateUser(userId, name, password, deptId, managerId, role, workTypeId);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "ユーザーを削除しました.";
    }
}
