package com.itbank.mall.dto.admin.grade;

import lombok.Data;

@Data
public class GradeRuleCreateRequestDto {
    private String gradeName;     // 생성 등급명
    private int minSpending;      // 최소 사용 금액
    private Integer displayOrder; // null -> 1
}
