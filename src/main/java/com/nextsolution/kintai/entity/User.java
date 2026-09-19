package com.nextsolution.kintai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String employeeCode;

    private String name;
    private String password;
    private String role;

    private Long deptId;
    private Long managerId;
    private Long workTypeId;

    private Double annualPaidLeave = 20.0;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
