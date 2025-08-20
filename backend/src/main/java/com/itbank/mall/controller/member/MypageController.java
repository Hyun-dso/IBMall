package com.itbank.mall.controller.member;

import com.itbank.mall.dto.member.MemberResponseDto;
import com.itbank.mall.dto.member.UpdateMemberRequestDto;
import com.itbank.mall.entity.member.Member;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.security.CustomUserDetails;
import com.itbank.mall.service.member.MemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MypageController {

    private final MemberService memberService;

    // ✅ 회원 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<MemberResponseDto>> getProfile(Authentication auth) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Member m = userDetails.getMember();

        MemberResponseDto dto = new MemberResponseDto(
            m.getId(), m.getEmail(), m.getName(), m.getNickname(), m.getPhone(),
            m.getAddress(), m.getProvider(), m.getVerified(), m.getGrade(), m.getRole(), m.getCreatedAt()
        );

        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    // ✅ 회원 정보 수정
    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @RequestBody UpdateMemberRequestDto dto,
            Authentication auth) {

        try {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long memberId = userDetails.getId();

            memberService.updateMemberInfo(memberId, dto);
            return ResponseEntity.ok(ApiResponse.ok(null, "회원 정보 수정 완료"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail("서버 오류가 발생했습니다"));
        }
    }
}
