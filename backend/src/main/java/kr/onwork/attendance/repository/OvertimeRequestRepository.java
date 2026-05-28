package kr.onwork.attendance.repository;

import java.time.LocalDateTime;
import java.util.List;
import kr.onwork.attendance.domain.OvertimeRequest;
import kr.onwork.attendance.domain.OvertimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OvertimeRequestRepository extends JpaRepository<OvertimeRequest, Long> {

    List<OvertimeRequest> findByUserIdOrderByIdDesc(Long userId);

    List<OvertimeRequest> findByUserIdInAndStatusOrderByIdDesc(List<Long> userIds, OvertimeStatus status);

    /** 결재 피로도 개선 #4: 장기 대기 결재 감지(에스컬레이션용). */
    List<OvertimeRequest> findByStatusAndCreatedAtBefore(OvertimeStatus status, LocalDateTime threshold);
}
