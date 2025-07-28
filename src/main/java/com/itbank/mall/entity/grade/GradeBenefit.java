package com.itbank.mall.entity.grade;

import lombok.Data;

@Data
public class GradeBenefit {
    private String gradeName;       // BASIC, VIP 등
    private int discountPercent;    // 할인률
    private int mileageRate;        // 마일리지 적립률
}
