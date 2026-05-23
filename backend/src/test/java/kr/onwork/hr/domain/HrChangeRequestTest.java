package kr.onwork.hr.domain;

import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** 인사 변경 요청 상태 전이 (PENDING → APPROVED/REJECTED, ADR-HR-001/002). */
class HrChangeRequestTest {

    @Test
    void create_startsPending() {
        HrChangeRequest r = HrChangeRequest.create(ChangeType.CREATE, null, Map.of("name", "홍길동"), "신규", 1L);
        assertThat(r.isPending()).isTrue();
        assertThat(r.getStatus()).isEqualTo(RequestStatus.PENDING);
    }

    @Test
    void approve_setsApproverAndProcessedAt() {
        HrChangeRequest r = HrChangeRequest.create(ChangeType.UPDATE, 5L, Map.of("position", "과장"), null, 3L);
        r.approve(1L);
        assertThat(r.getStatus()).isEqualTo(RequestStatus.APPROVED);
        assertThat(r.getApproverId()).isEqualTo(1L);
        assertThat(r.getProcessedAt()).isNotNull();
        assertThat(r.isPending()).isFalse();
    }

    @Test
    void reject_recordsReason() {
        HrChangeRequest r = HrChangeRequest.create(ChangeType.RESIGN, 5L, Map.of("resign_reason", "이직"), null, 3L);
        r.reject(1L, "서류 미비");
        assertThat(r.getStatus()).isEqualTo(RequestStatus.REJECTED);
        assertThat(r.getRejectReason()).isEqualTo("서류 미비");
    }
}
