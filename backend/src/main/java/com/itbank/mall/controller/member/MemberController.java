package com.itbank.mall.controller.member;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.member.MemberResponseDto;
import com.itbank.mall.dto.member.SignupRequestDto;
import com.itbank.mall.dto.member.UpdateMemberRequestDto;
import com.itbank.mall.entity.member.Member;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.security.CustomUserDetails;
import com.itbank.mall.service.member.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // ✅ 1. 회원가입
    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody SignupRequestDto dto) {
        try {
            memberService.signup(dto);
            return ApiResponse.ok(null, "회원가입 성공! 이메일 인증 시 비밀번호 찾기 등 추가로 정보를 받아 볼 수 있습니다.");
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail("서버 오류가 발생했습니다.");
        }
    }

    // ✅ 2. 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ApiResponse<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        boolean exists = memberService.existsByNickname(nickname);
        return ApiResponse.ok(Map.of("exists", exists));
    }

    // ✅ 3. 로그인 유저 정보 조회
    @GetMapping("/me")
    public ApiResponse<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return ApiResponse.fail("로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member m = userDetails.getMember();

        MemberResponseDto dto = new MemberResponseDto(
            m.getId(), m.getEmail(), m.getName(), m.getNickname(), m.getPhone(),
            m.getAddress(), m.getProvider(), m.getVerified(), m.getGrade(), m.getCreatedAt()
        );

        return ApiResponse.ok(dto);
    }

    // ✅ 4. 회원 정보 수정
    @PatchMapping("/me")
    public ApiResponse<String> updateMyInfo(@RequestBody UpdateMemberRequestDto dto, Authentication auth) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long memberId = userDetails.getId();

            memberService.updateMemberInfo(memberId, dto);
            return ApiResponse.ok(null, "회원 정보 수정 완료");
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail("서버 오류가 발생했습니다.");
        }
    }
}
