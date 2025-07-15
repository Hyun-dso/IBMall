package com.itbank.mall.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.LoginRequestDto;
import com.itbank.mall.dto.SignupRequestDto;
import com.itbank.mall.entity.Member;
import com.itbank.mall.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody LoginRequestDto dto, HttpSession session) {
        Member member = memberService.signin(dto.getEmail(), dto.getPassword());

        if (member == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "이메일 또는 비밀번호가 틀렸습니다"));
        }

        session.setAttribute("loginUser", member);
        return ResponseEntity.ok(Map.of("message", "로그인 성공"));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto dto) {
        try {
            memberService.signup(dto);
            return ResponseEntity.ok(Map.of("message", "회원가입 성공! 이메일 인증 링크를 확인하세요."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }
    
    @GetMapping("/api/nickname/check")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        boolean exists = memberService.existsByNickname(nickname);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    @GetMapping("/member/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");

        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }

        return ResponseEntity.ok(loginUser);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();  // 세션 무효화
        return ResponseEntity.ok(Map.of("message", "로그아웃 완료"));
    }
}
