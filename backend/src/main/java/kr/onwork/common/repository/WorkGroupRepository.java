package kr.onwork.common.repository;

import kr.onwork.common.domain.WorkGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkGroupRepository extends JpaRepository<WorkGroup, Long> {
}
