package com.itbank.mall.controller.member;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.member.coupon.MemberCouponDto;
import com.itbank.mall.entity.coupon.MemberCouponEntity;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.member.MemberCouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member-coupons")
@RequiredArgsConstructor
public class MemberCouponController {

    private final MemberCouponService memberCouponService;

    // ✅ [1] 회원에게 쿠폰 발급
    @PostMapping
    public ApiResponse<String> issueCoupon(@RequestBody MemberCouponDto dto) {
        int row = memberCouponService.issueCouponToMember(dto);
        return row > 0 ? ApiResponse.ok(null, "쿠폰 발급 성공") : ApiResponse.fail("쿠폰 발급 실패");
    }

    // ✅ [2] 내 유효한 쿠폰 목록 조회
    @GetMapping("/{memberId}")
    public ApiResponse<List<MemberCouponEntity>> getMyCoupons(@PathVariable int memberId) {
        List<MemberCouponEntity> list = memberCouponService.getValidCoupons(memberId);
        return ApiResponse.ok(list);
    }

    // ✅ [3] 쿠폰 사용 처리
    @PutMapping("/use/{memberCouponId}")
    public ApiResponse<String> useCoupon(@PathVariable int memberCouponId) {
        int row = memberCouponService.useCoupon(memberCouponId);
        return row > 0 ? ApiResponse.ok(null, "쿠폰 사용 완료") : ApiResponse.fail("쿠폰 사용 실패");
    }
}
