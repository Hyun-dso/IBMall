package com.itbank.mall.dto.admin.grade;

import lombok.Data;

@Data
public class GradeRuleUpdateRequestDto {
    private Long gradeId;       // 어떤 rule을 수정할지 지정
    private String gradeName;   // 수정할 등급명
    private int minSpending;    // 최소 사용 금액
    private Integer displayOrder;   // 표시 순서
}
