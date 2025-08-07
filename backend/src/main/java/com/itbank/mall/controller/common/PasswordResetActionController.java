package com.itbank.mall.controller.common;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.common.password.PasswordResetConfirmDto;
import com.itbank.mall.dto.common.password.PasswordResetRequestDto;
import com.itbank.mall.service.auth.PasswordResetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password")
public class PasswordResetActionController {

    private final PasswordResetService passwordResetService;

    // 1️⃣ 이메일로 비밀번호 재설정 링크 요청
    @PostMapping("/request")
    public ResponseEntity<?> requestReset(@RequestBody PasswordResetRequestDto dto) {
        passwordResetService.sendResetLink(dto.getEmail());

        return ResponseEntity.ok(Map.of("message", "비밀번호 재설정 메일이 발송되었습니다"));
    }

    // 2️⃣ 토큰으로 비밀번호 재설정
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetConfirmDto dto) {
        boolean success = passwordResetService.resetPassword(dto.getToken(), dto.getNewPassword());

        return success
                ? ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다"))
                : ResponseEntity.badRequest().body(Map.of("message", "토큰이 만료되었거나 잘못되었습니다"));
    }
}
