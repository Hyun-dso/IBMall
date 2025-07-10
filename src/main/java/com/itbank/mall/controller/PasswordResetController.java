package com.itbank.mall.controller;

import com.itbank.mall.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/send-reset-link")
    public ResponseEntity<?> sendResetLink(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        // 존재하는 회원인지 검증할 수도 있음
        passwordResetService.sendResetLink(email);

        return ResponseEntity.ok(Map.of("message", "비밀번호 재설정 링크가 이메일로 전송되었습니다."));
    }
    
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");

        boolean result = passwordResetService.resetPassword(token, newPassword);

        if (result) {
            return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "토큰이 유효하지 않거나 만료되었습니다"));
        }
    }
}
