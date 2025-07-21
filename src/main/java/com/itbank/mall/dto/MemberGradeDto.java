package com.itbank.mall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberGradeDto {
    private Long memberId;       // m.id
    private int totalSpent;      // 누적 주문 금액
    private String matchedGrade; // grade_rule에서 매칭된 등급
}

//등급 계산용 Dto 클래스입니다.