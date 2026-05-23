package kr.onwork.auth.service;

import io.jsonwebtoken.Claims;
import kr.onwork.auth.dto.LoginRequest;
import kr.onwork.auth.dto.LoginResponse;
import kr.onwork.auth.dto.TokenResponse;
import kr.onwork.auth.dto.UserInfo;
import kr.onwork.common.domain.User;
import kr.onwork.common.domain.UserCredential;
import kr.onwork.common.error.BusinessException;
import kr.onwork.common.error.ErrorCode;
import kr.onwork.common.repository.UserCredentialRepository;
import kr.onwork.common.repository.UserRepository;
import kr.onwork.common.security.JwtTokenProvider;
import kr.onwork.common.security.LoginAttemptService;
import kr.onwork.common.security.TokenBlacklistService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 인증 비즈니스 로직: 로그인(잠금 포함), 토큰 갱신, 로그아웃(블랙리스트), 내 정보. */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklistService blacklist;
    private final LoginAttemptService loginAttempt;

    public AuthService(UserRepository userRepository,
                       UserCredentialRepository credentialRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       TokenBlacklistService blacklist,
                       LoginAttemptService loginAttempt) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.blacklist = blacklist;
        this.loginAttempt = loginAttempt;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        String email = req.email();
        if (loginAttempt.isLocked(email)) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            loginAttempt.recordFailure(email);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.ACCOUNT_INACTIVE);
        }
        UserCredential cred = credentialRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(req.password(), cred.getPasswordHash())) {
            loginAttempt.recordFailure(email);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        loginAttempt.reset(email);
        String access = tokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refresh = tokenProvider.createRefreshToken(user.getId());
        return new LoginResponse(access, refresh, UserInfo.from(user));
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        Claims claims = tokenProvider.parse(refreshToken);
        if (!JwtTokenProvider.TYPE_REFRESH.equals(claims.get("type", String.class))) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        Long userId = Long.valueOf(claims.getSubject());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.ACCOUNT_INACTIVE);
        }
        String access = tokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        String newRefresh = tokenProvider.createRefreshToken(userId);
        return new TokenResponse(access, newRefresh);
    }

    public void logout(String accessToken) {
        if (accessToken == null) {
            return;
        }
        try {
            Claims claims = tokenProvider.parse(accessToken);
            blacklist.blacklist(accessToken, tokenProvider.remainingMillis(claims));
        } catch (RuntimeException ignored) {
            // 이미 만료/무효한 토큰은 블랙리스트 불필요
        }
    }

    @Transactional(readOnly = true)
    public UserInfo me(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        return UserInfo.from(user);
    }
}
