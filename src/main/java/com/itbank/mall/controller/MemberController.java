package com.itbank.mall.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.itbank.mall.dto.MemberResponseDto;
import com.itbank.mall.dto.SignupRequestDto;
import com.itbank.mall.dto.UpdateMemberRequestDto;
import com.itbank.mall.entity.Member;
import com.itbank.mall.security.CustomUserDetails;
import com.itbank.mall.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // ✅ 1. 회원가입
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

    // ✅ 2. 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        boolean exists = memberService.existsByNickname(nickname);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // ✅ 3. 로그인 유저 정보 조회
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("message", "로그인이 필요합니다."));
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member m = userDetails.getMember();

        MemberResponseDto dto = new MemberResponseDto(
            m.getId(), m.getEmail(), m.getName(), m.getNickname(), m.getPhone(),
            m.getAddress(), m.getProvider(), m.getVerified(), m.getGrade(), m.getCreatedAt()
        );

        return ResponseEntity.ok(dto);
    }

    // ✅ 4. 회원 정보 수정
    @PatchMapping("/me")
    public ResponseEntity<?> updateMyInfo(@RequestBody UpdateMemberRequestDto dto, Authentication auth) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long memberId = userDetails.getId();

            memberService.updateMemberInfo(memberId, dto);
            return ResponseEntity.ok(Map.of("message", "회원 정보 수정 완료"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }
}
