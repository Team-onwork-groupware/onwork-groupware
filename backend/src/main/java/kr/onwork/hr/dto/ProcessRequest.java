package kr.onwork.hr.dto;

import jakarta.validation.constraints.NotNull;

/** 인사 변경 요청 처리 (PATCH /hr/change-requests/{id}/process). REJECT 시 reason 필수(앱 검증). */
public record ProcessRequest(
        @NotNull Action action,
        String reason
) {
    public enum Action {
        APPROVE,
        REJECT
    }
}
