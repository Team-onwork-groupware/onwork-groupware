package kr.onwork.common.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.onwork.common.domain.Role;
import kr.onwork.common.domain.User;
import kr.onwork.common.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmployeeNo(String employeeNo);   // UC-HR-01 E1: 사번 중복 자동 재채번

    /** 결재 알림 대상(경영진 등) 조회. */
    List<User> findByRoleInAndStatus(Collection<Role> roles, UserStatus status);

    /** 직원 목록 검색 — 부서/상태/키워드(이름·사번) 선택 필터. */
    @Query("""
            select u from User u
            where (:departmentId is null or u.department.id = :departmentId)
              and (:status is null or u.status = :status)
              and (:keyword is null or u.name like %:keyword% or u.employeeNo like %:keyword%)
            order by u.id
            """)
    List<User> search(@Param("departmentId") Long departmentId,
                      @Param("status") UserStatus status,
                      @Param("keyword") String keyword);
}
