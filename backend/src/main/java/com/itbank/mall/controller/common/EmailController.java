package com.itbank.mall.controller.common;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.common.email.EmailCodeVerifyRequestDto;
import com.itbank.mall.dto.common.email.EmailVerificationRequestDto;
import com.itbank.mall.service.common.EmailVerificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/code/send")
    public ResponseEntity<?> sendCode(@RequestBody EmailVerificationRequestDto dto) {
        String email = dto.getEmail();
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
    public ResponseEntity<?> verify(@RequestBody EmailCodeVerifyRequestDto dto) {
        String email = dto.getEmail();
        String code = dto.getCode();

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
