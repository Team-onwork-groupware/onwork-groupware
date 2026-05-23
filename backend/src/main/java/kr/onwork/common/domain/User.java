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

    public boolean isResigned() {
        return status == UserStatus.RESIGNED;
    }

    /** 신규 입사자 생성 (UC-HR-01 승인 시점). */
    public static User createForHire(Department department, WorkGroup workGroup, String employeeNo,
                                     String name, String email, Role role, String position,
                                     LocalDate hireDate) {
        User u = new User();
        u.department = department;
        u.workGroup = workGroup;
        u.employeeNo = employeeNo;
        u.name = name;
        u.email = email;
        u.role = role != null ? role : Role.EMPLOYEE;
        u.position = position;
        u.status = UserStatus.ACTIVE;
        u.hireDate = hireDate;
        return u;
    }

    /** 인사정보 변경 (UC-HR-02 UPDATE 승인 시점). null이 아닌 필드만 반영. */
    public void applyUpdate(Department department, String position, Role role) {
        if (department != null) {
            this.department = department;
        }
        if (position != null) {
            this.position = position;
        }
        if (role != null) {
            this.role = role;
        }
    }

    /** 퇴사 처리 (UC-HR-03 승인 시점) — soft delete. */
    public void resign(LocalDate resignDate, String resignReason) {
        this.status = UserStatus.RESIGNED;
        this.resignDate = resignDate;
        this.resignReason = resignReason;
    }
}
