package kr.onwork.notification.domain;

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

/** 알림 — 모든 모듈 횡단 (notifications). 90일 보관 후 삭제 정책. */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(name = "ref_type", length = 20)
    private String refType;

    @Column(name = "ref_id")
    private Long refId;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Notification create(Long userId, String type, String refType, Long refId, String message) {
        Notification n = new Notification();
        n.userId = userId;
        n.type = type;
        n.refType = refType;
        n.refId = refId;
        n.message = message;
        n.isRead = false;
        return n;
    }

    public void markRead() {
        this.isRead = true;
    }
}
