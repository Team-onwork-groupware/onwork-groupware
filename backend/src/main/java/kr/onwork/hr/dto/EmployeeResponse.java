package kr.onwork.hr.dto;

import java.time.LocalDate;
import kr.onwork.common.domain.User;

/** 직원 목록/상세 응답. */
public record EmployeeResponse(
        Long id,
        String employeeNo,
        String name,
        String email,
        String role,
        String position,
        String status,
        String departmentName,
        Long departmentId,
        LocalDate hireDate,
        LocalDate resignDate,
        String resignReason
) {
    public static EmployeeResponse from(User u) {
        String deptName = u.getDepartment() != null ? u.getDepartment().getName() : null;
        Long deptId = u.getDepartment() != null ? u.getDepartment().getId() : null;
        return new EmployeeResponse(
                u.getId(),
                u.getEmployeeNo(),
                u.getName(),
                u.getEmail(),
                u.getRole().name(),
                u.getPosition(),
                u.getStatus().name(),
                deptName,
                deptId,
                u.getHireDate(),
                u.getResignDate(),
                u.getResignReason()
        );
    }
}
