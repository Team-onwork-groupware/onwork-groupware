package kr.onwork.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
