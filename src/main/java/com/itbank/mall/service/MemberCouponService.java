package com.itbank.mall.service;

import com.itbank.mall.dto.MemberCouponDto;
import com.itbank.mall.entity.MemberCouponEntity;
import com.itbank.mall.mapper.MemberCouponMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberCouponService {

    private final MemberCouponMapper memberCouponMapper;

    public MemberCouponService(MemberCouponMapper memberCouponMapper) {
        this.memberCouponMapper = memberCouponMapper;
    }

    // 회원에게 쿠폰 발급
    public int issueCouponToMember(MemberCouponDto dto) {
        return memberCouponMapper.insertMemberCoupon(dto);
    }

    // 사용 가능한 쿠폰 조회
    public List<MemberCouponEntity> getValidCoupons(int memberId) {
        return memberCouponMapper.selectValidCouponsByMemberId(memberId);
    }

    // 쿠폰 사용 처리
    public int useCoupon(int memberCouponId) {
        return memberCouponMapper.useCouponById(memberCouponId);
    }
}
