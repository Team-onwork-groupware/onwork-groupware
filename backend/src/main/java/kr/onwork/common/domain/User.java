package kr.onwork.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/** 직원 인사 정보 — 핵심 엔티티 (users). 결재 전 미생성, RESIGNED는 soft delete. */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_group_id", nullable = false)
    private WorkGroup workGroup;

    @Column(name = "employee_no", nullable = false, unique = true, length = 20)
    private String employeeNo;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(length = 20)
    private String position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "resign_date")
    private LocalDate resignDate;

    @Column(name = "resign_reason", length = 200)
    private String resignReason;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}
