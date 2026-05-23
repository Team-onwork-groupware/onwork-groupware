package kr.onwork.leave.domain;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/** 대행 결재자 라우팅 (ADR-003): 부재 시 대행자로 전환. */
class LeaveApproverTest {

    private LeaveApprover approver(Long approverId, Long delegateId, boolean absent) {
        LeaveApprover a = new LeaveApprover();
        ReflectionTestUtils.setField(a, "approverId", approverId);
        ReflectionTestUtils.setField(a, "delegateId", delegateId);
        ReflectionTestUtils.setField(a, "absent", absent);
        return a;
    }

    @Test
    void notAbsent_usesPrimaryApprover() {
        assertThat(approver(5L, 6L, false).activeApproverId()).isEqualTo(5L);
    }

    @Test
    void absent_switchesToDelegate() {
        assertThat(approver(5L, 6L, true).activeApproverId()).isEqualTo(6L);
    }

    @Test
    void absentWithoutDelegate_fallsBackToApprover() {
        assertThat(approver(5L, null, true).activeApproverId()).isEqualTo(5L);
    }
}
