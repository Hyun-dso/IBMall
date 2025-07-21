package com.itbank.mall.service;

import com.itbank.mall.dto.CouponDto;
import com.itbank.mall.entity.CouponEntity;
import com.itbank.mall.mapper.CouponMapper;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
