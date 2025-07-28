package com.itbank.mall.dto.member.coupon;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberCouponDto {
    private int memberId;
    private int couponId;
    private int memberCouponId;
    private String code;
    private int discountPercent;
    private boolean isUsed;
    private LocalDateTime expiresAt;
    private LocalDateTime issuedAt;
}
