package kr.onwork.approval.service;

import kr.onwork.approval.domain.ApprovalRoute;
import kr.onwork.approval.repository.ApprovalRouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApprovalRoutingService {

    public static final String TYPE_LEAVE = "LEAVE";
    public static final String TYPE_OVERTIME = "ATTENDANCE";
    public static final String TYPE_HR = "HR";

    private final ApprovalRouteRepository routeRepository;

    public ApprovalRoutingService(ApprovalRouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Transactional
    public void open(String type, Long refId, Long requesterId, Long approverId, Long departmentId) {
        if (departmentId == null || routeRepository.findByTypeAndRefId(type, refId).isPresent()) {
            return;
        }
        routeRepository.save(ApprovalRoute.pending(type, refId, requesterId, approverId, departmentId));
    }

    @Transactional
    public void complete(String type, Long refId, Long approverId, String action, String reason) {
        routeRepository.findByTypeAndRefId(type, refId)
                .ifPresent(route -> route.complete(approverId, action, reason));
    }

    @Transactional
    public void cancel(String type, Long refId, String reason) {
        routeRepository.findByTypeAndRefId(type, refId)
                .ifPresent(route -> route.cancel(reason));
    }
}
