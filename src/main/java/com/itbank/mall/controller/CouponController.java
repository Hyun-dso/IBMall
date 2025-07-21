package com.itbank.mall.controller;

import com.itbank.mall.dto.CouponDto;
import com.itbank.mall.entity.CouponEntity;
import com.itbank.mall.service.CouponService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // 쿠폰 등록 (관리자용)
    @PostMapping
    public String addCoupon(@RequestBody CouponDto dto) {
        int row = couponService.addCoupon(dto);
        return row > 0 ? "등록 성공" : "등록 실패";
    }

    // 전체 쿠폰 조회
    @GetMapping
    public List<CouponEntity> getAllCoupons() {
        return couponService.getAllCoupons();
    }
}
