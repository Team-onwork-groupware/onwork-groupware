package kr.onwork.hr.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import kr.onwork.hr.domain.ChangeType;

/**
 * 인사 변경 요청 등록 (POST /hr/change-requests) — 입사/수정/퇴사 통합.
 * payload는 change_type별 상이: CREATE(입사정보) / UPDATE(변경필드) / RESIGN(퇴사일·사유).
 */
public record CreateChangeRequestRequest(
        @NotNull ChangeType changeType,
        Long targetUserId,
        @NotNull Map<String, Object> payload,
        String reason
) {
}
