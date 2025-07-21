package com.itbank.mall.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberCouponEntity {
    private int memberCouponId;
    private int memberId;
    private int couponId;
    private boolean isUsed;
    private LocalDateTime issuedAt;
    private String code;
    private int discountPercent;
    private LocalDateTime expiresAt;
}
