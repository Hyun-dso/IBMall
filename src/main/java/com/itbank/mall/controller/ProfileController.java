package com.itbank.mall.controller;

import com.itbank.mall.dto.MemberResponseDto;
import com.itbank.mall.dto.UpdateMemberRequestDto;
import com.itbank.mall.entity.Member;
import com.itbank.mall.security.CustomUserDetails;
import com.itbank.mall.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class ProfileController {

    private final MemberService memberService;

    // ✅ 회원 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Member m = userDetails.getMember();

        MemberResponseDto dto = new MemberResponseDto(
            m.getId(), m.getEmail(), m.getName(), m.getNickname(), m.getPhone(),
            m.getAddress(), m.getProvider(), m.getVerified(), m.getGrade(), m.getCreatedAt()
        );

        return ResponseEntity.ok(dto);
    }

    // ✅ 회원 정보 수정
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateMemberRequestDto dto, Authentication auth) {
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
                    .body(Map.of("message", "서버 오류가 발생했습니다"));
        }
    }
}
