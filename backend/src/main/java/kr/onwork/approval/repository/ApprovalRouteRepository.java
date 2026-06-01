package kr.onwork.approval.repository;

import java.util.Optional;
import kr.onwork.approval.domain.ApprovalRoute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRouteRepository extends JpaRepository<ApprovalRoute, Long> {
    Optional<ApprovalRoute> findByTypeAndRefId(String type, Long refId);
}
