package kr.onwork.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import static lombok.AccessLevel.PROTECTED;

/** 인사 변경 요청 — 승인 전 실데이터 미반영(ADR-HR-001). PENDING으로 저장, 승인 시점에만 users 반영. */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "hr_change_requests")
public class HrChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false, length = 20)
    private ChangeType changeType;

    @Column(name = "target_user_id")
    private Long targetUserId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status;

    @Column(name = "requested_by", nullable = false)
    private Long requestedBy;

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "batch_id", length = 36)
    private String batchId;

    public static HrChangeRequest create(ChangeType type, Long targetUserId,
                                         Map<String, Object> payload, String reason, Long requestedBy) {
        HrChangeRequest r = new HrChangeRequest();
        r.changeType = type;
        r.targetUserId = targetUserId;
        r.payload = payload;
        r.reason = reason;
        r.requestedBy = requestedBy;
        r.status = RequestStatus.PENDING;
        return r;
    }

    public boolean isPending() {
        return status == RequestStatus.PENDING;
    }

    public void approve(Long approverId) {
        this.status = RequestStatus.APPROVED;
        this.approverId = approverId;
        this.processedAt = LocalDateTime.now();
    }

    public void reject(Long approverId, String rejectReason) {
        this.status = RequestStatus.REJECTED;
        this.approverId = approverId;
        this.rejectReason = rejectReason;
        this.processedAt = LocalDateTime.now();
    }
}
