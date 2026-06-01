package kr.onwork.hr.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record HrBatchProcessRequest(
        @NotEmpty @Size(max = 50) List<Long> ids,
        @NotNull Action action
) {
    public enum Action {
        APPROVE
    }
}
