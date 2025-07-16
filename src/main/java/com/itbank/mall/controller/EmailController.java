package com.itbank.mall.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itbank.mall.service.EmailVerificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/code/send")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        try {
            emailVerificationService.sendVerificationCode(email);
            return ResponseEntity.ok(Map.of("message", "이메일 인증코드 전송 완료"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "이메일 전송 실패"));
        }
    }

    @PostMapping("/code/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");

        try {
            boolean success = emailVerificationService.verifyCode(email, code);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "이메일 인증 성공"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body(Map.of("message", "인증 실패 또는 만료된 코드입니다"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }
}
