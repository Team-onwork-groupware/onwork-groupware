package kr.onwork.common.error;

/** API 에러 응답 본문 — {code, message} (API 명세 공통 규칙). */
public record ErrorResponse(String code, String message) {

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.name(), message);
    }
}
