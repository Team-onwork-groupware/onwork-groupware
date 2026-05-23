package kr.onwork.auth.web;

import jakarta.validation.Valid;
import kr.onwork.auth.dto.LoginRequest;
import kr.onwork.auth.dto.LoginResponse;
import kr.onwork.auth.dto.RefreshRequest;
import kr.onwork.auth.dto.TokenResponse;
import kr.onwork.auth.dto.UserInfo;
import kr.onwork.auth.service.AuthService;
import kr.onwork.common.security.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 인증 엔드포인트 (/api/v1/auth). login·refresh는 공개, me·logout은 인증 필요. */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest req) {
        return authService.refresh(req.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7) : null;
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public UserInfo me() {
        return authService.me(SecurityUtil.currentUserId());
    }
}
