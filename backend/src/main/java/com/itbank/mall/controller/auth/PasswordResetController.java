package com.itbank.mall.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.auth.ResetLinkRequestDto;
import com.itbank.mall.dto.auth.ResetPasswordRequestDto;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.auth.PasswordResetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password/reset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/link")
    public ResponseEntity<ApiResponse<String>> sendResetLink(@RequestBody ResetLinkRequestDto dto) {
        try {
            passwordResetService.sendResetLink(dto.getEmail());
            return ResponseEntity.ok(ApiResponse.ok("비밀번호 재설정 링크가 이메일로 전송되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail("링크 전송 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequestDto dto) {
        try {
            boolean result = passwordResetService.resetPassword(dto.getToken(), dto.getNewPassword());

            if (result) {
                return ResponseEntity.ok(ApiResponse.ok("비밀번호가 성공적으로 변경되었습니다."));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.fail("토큰이 유효하지 않거나 만료되었습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail("비밀번호 변경 중 오류가 발생했습니다."));
        }
    }
}
