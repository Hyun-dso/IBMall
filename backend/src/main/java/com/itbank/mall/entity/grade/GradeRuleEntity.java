package com.itbank.mall.entity.grade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeRuleEntity {
    private Long gradeId;
    private String gradeName;
    private int minSpending;
    private int displayOrder;

    public GradeRuleEntity() {} // ✅ 있어야 안전

    public GradeRuleEntity(String gradeName, int minSpending) {
        this.gradeName = gradeName;
        this.minSpending = minSpending;
    }
}
