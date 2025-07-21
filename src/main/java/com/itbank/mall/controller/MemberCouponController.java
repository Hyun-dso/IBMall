package com.itbank.mall.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.MemberCouponDto;
import com.itbank.mall.entity.MemberCouponEntity;
import com.itbank.mall.service.MemberCouponService;

@RestController
@RequestMapping("/api/member-coupons")
public class MemberCouponController {

    private final MemberCouponService memberCouponService;

    public MemberCouponController(MemberCouponService memberCouponService) {
        this.memberCouponService = memberCouponService;
    }

    // [1] 회원에게 쿠폰 발급
    @PostMapping
    public String issueCoupon(@RequestBody MemberCouponDto dto) {
        int row = memberCouponService.issueCouponToMember(dto);
        return row > 0 ? "쿠폰 발급 성공" : "쿠폰 발급 실패";
    }

    // [2] 내 유효한 쿠폰 목록 조회
    @GetMapping("/{memberId}")
    public List<MemberCouponEntity> getMyCoupons(@PathVariable ("memberId") int memberId) {
        return memberCouponService.getValidCoupons(memberId);
    }

    // [3] 쿠폰 사용 처리
    @PutMapping("/use/{memberCouponId}")
    public String useCoupon(@PathVariable ("memberCouponId") int memberCouponId) {
        int row = memberCouponService.useCoupon(memberCouponId);
        return row > 0 ? "쿠폰 사용 완료" : "사용 실패";
    }
}
