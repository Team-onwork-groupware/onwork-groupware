package kr.onwork.notification.repository;

import java.util.List;
import kr.onwork.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByIdDesc(Long userId);

    List<Notification> findByUserIdAndIsReadFalseOrderByIdDesc(Long userId);

    long countByUserIdAndIsReadFalse(Long userId);
}
