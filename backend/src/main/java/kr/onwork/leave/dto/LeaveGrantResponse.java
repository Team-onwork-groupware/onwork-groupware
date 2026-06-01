package kr.onwork.leave.dto;

import java.util.List;

public record LeaveGrantResponse(
        int total,
        int successCount,
        int failureCount,
        List<Result> results
) {
    public record Result(Long userId, boolean ok, String error) {
    }
}
