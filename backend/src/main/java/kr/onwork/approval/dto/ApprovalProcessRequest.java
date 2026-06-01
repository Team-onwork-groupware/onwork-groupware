package kr.onwork.approval.dto;

import jakarta.validation.constraints.NotNull;

public record ApprovalProcessRequest(
        @NotNull String type,
        @NotNull Action action,
        String reason
) {
    public enum Action {
        APPROVE,
        ON_HOLD,
        REJECT
    }
}
