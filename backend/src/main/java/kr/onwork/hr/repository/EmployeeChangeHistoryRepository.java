package kr.onwork.hr.repository;

import kr.onwork.hr.domain.EmployeeChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeChangeHistoryRepository extends JpaRepository<EmployeeChangeHistory, Long> {
}
