package com.itbank.mall.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itbank.mall.dto.SigninRequestDto;
import com.itbank.mall.entity.Member;
import com.itbank.mall.service.MemberService;
import com.itbank.mall.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninRequestDto dto) {
        try {
            Member member = memberService.signin(dto.getEmail(), dto.getPassword());

            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(Map.of("message", "이메일 또는 비밀번호가 틀렸습니다"));
            }

            // ✅ ID + EMAIL 함께 토큰에 넣기
            String token = jwtUtil.generateToken(member.getId(), member.getEmail());

            return ResponseEntity.ok(Map.of(
                "message", "로그인 성공",
                "token", token
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "로그아웃 완료 (프론트에서 토큰 삭제 필요)"));
    }
}
