package kr.onwork.hr.repository;

import java.time.LocalDateTime;
import java.util.List;
import kr.onwork.hr.domain.HrChangeRequest;
import kr.onwork.hr.domain.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HrChangeRequestRepository extends JpaRepository<HrChangeRequest, Long> {

    List<HrChangeRequest> findByStatusOrderByIdDesc(RequestStatus status);

    List<HrChangeRequest> findAllByOrderByIdDesc();

    List<HrChangeRequest> findByRequestedByOrderByIdDesc(Long requestedBy);

    /** 결재 피로도 개선 #4: 장기 대기 결재 감지(에스컬레이션용). */
    List<HrChangeRequest> findByStatusAndCreatedAtBefore(RequestStatus status, LocalDateTime threshold);
}
