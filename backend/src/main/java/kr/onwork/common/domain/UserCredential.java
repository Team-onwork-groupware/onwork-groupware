package kr.onwork.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/** 직원 인증 정보 — 3NF 분리(users와 별도), bcrypt 비밀번호 (user_credentials). */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "user_credentials")
public class UserCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
