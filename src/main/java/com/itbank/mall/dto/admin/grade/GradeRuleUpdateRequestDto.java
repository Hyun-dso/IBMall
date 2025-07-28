package com.itbank.mall.dto.admin.grade;

import lombok.Data;

@Data
public class GradeRuleUpdateRequestDto {
    private String gradeName;
    private int minSpending;
}
