package com.itbank.mall.mapper;

import com.itbank.mall.dto.CouponDto;
import com.itbank.mall.entity.CouponEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CouponMapper {
    int insertCoupon(CouponDto dto);
    List<CouponEntity> selectAllCoupons();
}
