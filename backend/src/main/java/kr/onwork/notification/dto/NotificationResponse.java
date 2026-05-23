package kr.onwork.notification.dto;

import java.time.LocalDateTime;
import kr.onwork.notification.domain.Notification;

public record NotificationResponse(
        Long id,
        String type,
        String message,
        String refType,
        Long refId,
        boolean read,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(n.getId(), n.getType(), n.getMessage(),
                n.getRefType(), n.getRefId(), n.isRead(), n.getCreatedAt());
    }
}
