package com.itbank.mall.controller;

import com.itbank.mall.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/reset-link")
    public ResponseEntity<?> sendResetLink(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        try {
            passwordResetService.sendResetLink(email);
            return ResponseEntity.ok(Map.of("message", "비밀번호 재설정 링크가 이메일로 전송되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "링크 전송 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");

        try {
            boolean result = passwordResetService.resetPassword(token, newPassword);

            if (result) {
                return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body(Map.of("message", "토큰이 유효하지 않거나 만료되었습니다"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "비밀번호 변경 중 오류가 발생했습니다."));
        }
    }
}
