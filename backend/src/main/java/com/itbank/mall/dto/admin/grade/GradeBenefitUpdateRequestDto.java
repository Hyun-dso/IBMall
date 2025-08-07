package com.itbank.mall.dto.admin.grade;

import lombok.Data;

@Data
public class GradeBenefitUpdateRequestDto {
    private int discountPercent;
    private int mileageRate;
}
