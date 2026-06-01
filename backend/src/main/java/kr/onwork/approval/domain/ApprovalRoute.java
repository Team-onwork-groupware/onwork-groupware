package kr.onwork.approval.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/** 결재 라우팅 인덱스 (approvals). 각 업무 요청의 대기/완료 상태를 결재함 관점으로 동기화한다. */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "approvals")
public class ApprovalRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(name = "ref_id", nullable = false)
    private Long refId;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Column(name = "approver_id")
    private Long approverId;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 20)
    private String action;

    @Column(length = 500)
    private String reason;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public static ApprovalRoute pending(String type, Long refId, Long requesterId,
                                        Long approverId, Long departmentId) {
        ApprovalRoute route = new ApprovalRoute();
        route.type = type;
        route.refId = refId;
        route.requesterId = requesterId;
        route.approverId = approverId;
        route.departmentId = departmentId;
        route.status = "PENDING";
        return route;
    }

    public void complete(Long approverId, String action, String reason) {
        this.status = "COMPLETED";
        this.approverId = approverId;
        this.action = action;
        this.reason = reason;
        this.processedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        this.status = "CANCELLED";
        this.reason = reason;
        this.processedAt = LocalDateTime.now();
    }
}
