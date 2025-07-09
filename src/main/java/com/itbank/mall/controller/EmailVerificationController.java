package com.itbank.mall.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.service.EmailVerificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    public ResponseEntity<?> sendCode(@RequestParam String email) {
        emailVerificationService.sendVerificationCode(email);
        return ResponseEntity.ok("이메일 인증코드 전송 완료");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String email,
                                    @RequestParam String code) {
        boolean success = emailVerificationService.verifyCode(email, code);
        if (success) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.badRequest().body("인증 실패 또는 만료된 코드입니다");
        }
    }
}