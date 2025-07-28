package com.itbank.mall.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.itbank.mall.dto.admin.coupon.CouponDto;
import com.itbank.mall.entity.coupon.CouponEntity;

@Mapper
public interface CouponMapper {
    int insertCoupon(CouponDto dto);
    List<CouponEntity> selectAllCoupons();
}
