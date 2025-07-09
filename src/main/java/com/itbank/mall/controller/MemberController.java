package com.itbank.mall.controller;

import com.itbank.mall.dto.SignupRequestDto;
import com.itbank.mall.service.MemberService;
import com.itbank.mall.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto dto) {
        memberService.signup(dto);
        return ResponseEntity.ok("회원가입 성공! 이메일 인증 링크를 확인하세요.");
    }
}
