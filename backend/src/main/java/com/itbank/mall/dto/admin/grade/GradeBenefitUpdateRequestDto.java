package com.itbank.mall.dto.admin.grade;

import lombok.Data;

@Data
public class GradeBenefitUpdateRequestDto {
    private Integer discountPercent;
    private Integer mileageRate;
}
