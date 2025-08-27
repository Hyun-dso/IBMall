package com.itbank.mall.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.admin.coupon.CouponDto;
import com.itbank.mall.entity.coupon.CouponEntity;
import com.itbank.mall.mapper.admin.CouponMapper;

@Service
public class CouponService {

    private final CouponMapper couponMapper;

    public CouponService(CouponMapper couponMapper) {
        this.couponMapper = couponMapper;
    }

    // 쿠폰 등록
    public int addCoupon(CouponDto dto) {
        return couponMapper.insertCoupon(dto);
    }

    // 전체 쿠폰 조회
    public List<CouponEntity> getAllCoupons() {
        return couponMapper.selectAllCoupons();
    }
    // 쿠폰 삭제
    public int deleteCoupon(Long couponId) {
        return couponMapper.deleteCouponById(couponId);
    }

}
