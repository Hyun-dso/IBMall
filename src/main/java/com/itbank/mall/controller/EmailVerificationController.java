package com.itbank.mall.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.entity.Member;
import com.itbank.mall.service.EmailVerificationService;
import com.itbank.mall.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final MemberService memberService;

    @PostMapping("/send")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        emailVerificationService.sendVerificationCode(email);
        return ResponseEntity.ok(Map.of("message", "이메일 인증코드 전송 완료"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> payload, HttpSession session) {
        String email = payload.get("email");
        String code = payload.get("code");

        boolean success = emailVerificationService.verifyCode(email, code);
        
        if (success) {
            Member updated = memberService.findByEmail(email);
            session.setAttribute("loginUser", updated); // 세션 덮어쓰기
            return ResponseEntity.ok(Map.of("message", "인증 성공"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "인증 실패 또는 만료된 코드입니다"));
        }
    }
}