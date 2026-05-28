package kr.onwork.approval.dto;

import java.util.List;

public record BatchProcessResponse(
        int total,
        int succeeded,
        int failed,
        List<Result> results
) {
    public record Result(String type, Long id, boolean ok, String error) {}
}
