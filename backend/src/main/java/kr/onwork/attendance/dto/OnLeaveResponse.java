package kr.onwork.attendance.dto;

import java.time.LocalDate;

/** 오늘 휴가 중인 직원 표시용 — 이름·부서·휴가 기간. */
public record OnLeaveResponse(
        Long userId,
        String userName,
        String departmentName,
        LocalDate startDate,
        LocalDate endDate
) {
}
