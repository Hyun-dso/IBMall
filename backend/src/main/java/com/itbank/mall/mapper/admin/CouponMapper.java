package com.itbank.mall.mapper.admin;

import com.itbank.mall.dto.admin.coupon.CouponDto;
import com.itbank.mall.entity.coupon.CouponEntity;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CouponMapper {
    int insertCoupon(CouponDto dto);
    List<CouponEntity> selectAllCoupons();
}
