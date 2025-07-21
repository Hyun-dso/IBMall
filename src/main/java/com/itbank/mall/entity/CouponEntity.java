package com.itbank.mall.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponEntity {
    private int couponId;
    private String code;
    private int discountPercent;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
