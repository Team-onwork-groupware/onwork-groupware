package kr.onwork.hr.dto;

import java.util.List;
import java.util.UUID;

public record HrBatchProcessResponse(
        UUID batchId,
        int total,
        int successCount,
        int failureCount,
        List<Result> results
) {
    public record Result(Long id, boolean ok, String error) {
    }
}
