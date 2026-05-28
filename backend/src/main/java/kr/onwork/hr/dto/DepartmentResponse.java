package kr.onwork.hr.dto;

/** 인사 화면 부서 드롭다운용. UC-HR-01 A2 미분류는 프론트에서 빈 옵션으로 추가. */
public record DepartmentResponse(Long id, String name) {
}
