package kr.onwork.common.repository;

import java.util.Optional;
import kr.onwork.common.domain.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    Optional<UserCredential> findByUserId(Long userId);
}
