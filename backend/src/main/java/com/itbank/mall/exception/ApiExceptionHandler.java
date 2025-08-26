package com.itbank.mall.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.itbank.mall.response.ApiResponse;

import jakarta.validation.ConstraintViolationException;

/**
 * 전역 예외 핸들러
 * - 서비스에서 던진 예외를 표준 ApiResponse로 변환
 * - HTTP 상태코드도 함께 설정
 *
 * 매핑 규칙(이번 결제/재고 작업 기준):
 *  - IllegalStateException  -> 409 CONFLICT   (재고 부족/동시성 실패 등)
 *  - IllegalArgumentException -> 422 UNPROCESSABLE_ENTITY (금액 불일치/검증 실패 등)
 *  - MethodArgumentNotValidException / ConstraintViolationException / MissingServletRequestParameterException -> 400 BAD_REQUEST
 *  - AccessDeniedException -> 401/403 (보안)
 *  - 그 외 -> 500 INTERNAL_SERVER_ERROR
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /* ====== 400 Bad Request 계열 ====== */

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ApiResponse<String>> handleBadRequest(Exception ex) {
        String message = extractValidationMessage(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(message));
    }

    /* ====== 401/403 보안 계열 (선택: Spring Security 사용 시) ====== */

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(AccessDeniedException ex) {
        // 인증은 되었지만 권한이 부족한 경우 403
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail("접근 권한이 없습니다."));
    }

    /* ====== 409 재고/동시성 ====== */

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalState(IllegalStateException ex) {
        // 재고 부족/동시성 실패 등
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(ex.getMessage()));
    }

    /* ====== 422 결제 검증 실패/금액 불일치 ====== */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.fail(ex.getMessage()));
    }
    
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> badCred(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail("이메일 또는 비밀번호가 올바르지 않습니다."));
    }


    /* ====== 500 그 외 예외 ====== */
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleAny(Exception ex) {
        // 운영에서는 로깅 프레임워크로 스택트레이스 기록 권장
    	ex.printStackTrace(); // (또는 log.error("Unhandled", ex))
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("서버 오류가 발생했습니다."));
    }

    /* ====== 내부 유틸 ====== */

    private String extractValidationMessage(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException manve) {
            var fieldError = manve.getBindingResult().getFieldError();
            if (fieldError != null) {
                return fieldError.getField() + ": " + fieldError.getDefaultMessage();
            }
            return "요청 형식이 올바르지 않습니다.";
        }
        if (ex instanceof ConstraintViolationException cve) {
            return cve.getConstraintViolations().stream()
                    .findFirst()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .orElse("요청 형식이 올바르지 않습니다.");
        }
        if (ex instanceof MissingServletRequestParameterException msrpe) {
            return "필수 파라미터 누락: " + msrpe.getParameterName();
        }
        return "요청 형식이 올바르지 않습니다.";
    }
    

}
