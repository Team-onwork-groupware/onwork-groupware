package kr.onwork.approval.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** 일괄 처리 요청 — 결재 피로도 개선 #2. 항목별 개별 트랜잭션. */
public record BatchProcessRequest(
        @NotEmpty List<Item> items,
        @NotNull Action action,
        String reason
) {
    public record Item(@NotNull String type, @NotNull Long id) {}
    public enum Action { APPROVE, REJECT }
}
