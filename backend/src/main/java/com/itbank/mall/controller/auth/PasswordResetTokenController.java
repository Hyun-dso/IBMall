package com.itbank.mall.controller.auth;

import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.auth.PasswordResetService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password/reset")
public class PasswordResetTokenController {

    private final PasswordResetService passwordResetService;

    // ✅ 비밀번호 재설정 토큰 유효성 확인
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyToken(@RequestParam("token") String token) {
        try {
            boolean isValid = passwordResetService.validateResetToken(token);

            if (isValid) {
                return ResponseEntity.ok(ApiResponse.ok("유효한 토큰입니다."));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.fail("토큰이 유효하지 않거나 만료되었습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail("토큰 검증 중 오류가 발생했습니다."));
        }
    }
}
