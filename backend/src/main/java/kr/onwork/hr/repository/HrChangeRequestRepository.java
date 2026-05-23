package kr.onwork.hr.repository;

import java.util.List;
import kr.onwork.hr.domain.HrChangeRequest;
import kr.onwork.hr.domain.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HrChangeRequestRepository extends JpaRepository<HrChangeRequest, Long> {

    List<HrChangeRequest> findByStatusOrderByIdDesc(RequestStatus status);

    List<HrChangeRequest> findAllByOrderByIdDesc();

    List<HrChangeRequest> findByRequestedByOrderByIdDesc(Long requestedBy);
}
