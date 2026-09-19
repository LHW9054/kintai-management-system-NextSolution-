package com.nextsolution.kintai.controller;

import com.nextsolution.kintai.entity.Department;
import com.nextsolution.kintai.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 部署管理（システム管理者専用）
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @PostMapping
    public Department createDepartment(@RequestParam String deptName, @RequestParam String location) {
        return departmentService.createDepartment(deptName, location);
    }

    @PutMapping("/{deptId}")
    public Department updateDepartment(@PathVariable Long deptId,
                                        @RequestParam String deptName,
                                        @RequestParam String location) {
        return departmentService.updateDepartment(deptId, deptName, location);
    }

    @DeleteMapping("/{deptId}")
    public String deleteDepartment(@PathVariable Long deptId) {
        departmentService.deleteDepartment(deptId);
        return "部署を削除しました.";
    }
}
