package com.itbank.mall.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeRuleEntity {
    private String gradeName;
    private int minSpending;

    public GradeRuleEntity() {}  // ✅ 기본 생성자 추가

    public GradeRuleEntity(String gradeName, int minSpending) {
        this.gradeName = gradeName;
        this.minSpending = minSpending;
    }
}
