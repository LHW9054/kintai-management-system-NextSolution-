package com.nextsolution.kintai.service;

import com.nextsolution.kintai.entity.Department;
import com.nextsolution.kintai.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department createDepartment(String deptName, String location) {
        Department dept = new Department();
        dept.setDeptName(deptName);
        dept.setLocation(location);
        return departmentRepository.save(dept);
    }

    public Department updateDepartment(Long deptId, String deptName, String location) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new IllegalArgumentException("部署が存在しません."));
        dept.setDeptName(deptName);
        dept.setLocation(location);
        return departmentRepository.save(dept);
    }

    public void deleteDepartment(Long deptId) {
        departmentRepository.deleteById(deptId);
    }
}
