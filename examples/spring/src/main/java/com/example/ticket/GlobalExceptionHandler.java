package com.example.ticket;

import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 05장 본문의 핸들러를 그대로 옮긴 것. Exception 전체를 잡는 핸들러가 어떤 부작용을 내는지 테스트로 확인한다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode.getCode(), e.getMessage(), Instant.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(Exception e) {
        return ResponseEntity.status(500)
                .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "서버에서 오류가 발생했습니다.", Instant.now()));
    }

    public record ErrorResponse(String code, String message, Instant timestamp) {}
}
