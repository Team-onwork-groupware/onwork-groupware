package kr.onwork.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UserInfo user
) {
}
