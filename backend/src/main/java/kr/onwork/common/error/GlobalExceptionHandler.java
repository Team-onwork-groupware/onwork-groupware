package kr.onwork.common.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 전역 예외 처리 — 모든 예외를 {code, message} 형식으로 변환. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        ErrorCode code = ex.getErrorCode();
        return ResponseEntity.status(code.status())
                .body(ErrorResponse.of(code, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String field = ex.getBindingResult().getFieldErrors().stream().findFirst()
                .map(e -> e.getField()).orElse("");
        String msg = ErrorCode.INVALID_PAYLOAD.defaultMessage()
                + (field.isEmpty() ? "" : " (필드: " + field + ")");
        return ResponseEntity.status(ErrorCode.INVALID_PAYLOAD.status())
                .body(ErrorResponse.of(ErrorCode.INVALID_PAYLOAD, msg));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(ErrorCode.FORBIDDEN.status())
                .body(ErrorResponse.of(ErrorCode.FORBIDDEN, ErrorCode.FORBIDDEN.defaultMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.status())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.defaultMessage()));
    }
}
