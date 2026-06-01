package kr.onwork.attendance.repository;

import java.util.Collection;
import java.util.List;
import kr.onwork.attendance.domain.MonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlySummaryRepository extends JpaRepository<MonthlySummary, Long> {
    boolean existsByUserIdInAndYearMonth(Collection<Long> userIds, String yearMonth);

    List<MonthlySummary> findByUserIdInAndYearMonth(Collection<Long> userIds, String yearMonth);
}
