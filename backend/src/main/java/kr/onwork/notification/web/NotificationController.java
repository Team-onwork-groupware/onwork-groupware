package kr.onwork.notification.web;

import java.util.List;
import java.util.Map;
import kr.onwork.common.security.SecurityUtil;
import kr.onwork.notification.dto.NotificationResponse;
import kr.onwork.notification.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 알림 API (/api/v1/notifications). */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public Map<String, Object> list() {
        Long uid = SecurityUtil.currentUserId();
        List<NotificationResponse> items = notificationService.list(uid);
        return Map.of("total", items.size(), "unread", notificationService.unreadCount(uid), "items", items);
    }

    @GetMapping("/unread-count")
    public Map<String, Object> unreadCount() {
        return Map.of("unread", notificationService.unreadCount(SecurityUtil.currentUserId()));
    }

    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        notificationService.markRead(SecurityUtil.currentUserId(), id);
    }

    @PatchMapping("/read-all")
    public void markAllRead() {
        notificationService.markAllRead(SecurityUtil.currentUserId());
    }
}
