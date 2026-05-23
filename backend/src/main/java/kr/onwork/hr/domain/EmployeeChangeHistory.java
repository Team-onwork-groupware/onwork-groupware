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

/** 인사 변경 적용 이력 — 승인 완료 후 before/after 스냅샷(감사 추적, ADR-HR-001). */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "employee_change_histories")
public class EmployeeChangeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false, length = 20)
    private ChangeType changeType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "before_value", columnDefinition = "jsonb")
    private Map<String, Object> beforeValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "after_value", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> afterValue;

    @Column(name = "change_request_id")
    private Long changeRequestId;

    @Column(name = "changed_by", nullable = false)
    private Long changedBy;

    @Column(name = "changed_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime changedAt;

    public static EmployeeChangeHistory record(Long targetUserId, ChangeType type,
                                               Map<String, Object> before, Map<String, Object> after,
                                               Long changeRequestId, Long changedBy) {
        EmployeeChangeHistory h = new EmployeeChangeHistory();
        h.targetUserId = targetUserId;
        h.changeType = type;
        h.beforeValue = before;
        h.afterValue = after;
        h.changeRequestId = changeRequestId;
        h.changedBy = changedBy;
        return h;
    }
}
