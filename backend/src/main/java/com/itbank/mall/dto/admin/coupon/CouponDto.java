package com.itbank.mall.dto.admin.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto {
    private String code;
    private int discountPercent;
    private LocalDateTime expiresAt;
}
