package com.itbank.mall.mapper;

import com.itbank.mall.dto.MemberCouponDto;
import com.itbank.mall.entity.MemberCouponEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberCouponMapper {

    // 쿠폰 발급
    int insertMemberCoupon(MemberCouponDto dto);

    // 유효한 쿠폰 목록 조회
    List<MemberCouponEntity> selectValidCouponsByMemberId(int memberId);

    // 쿠폰 사용 처리
    int useCouponById(int memberCouponId);
}
