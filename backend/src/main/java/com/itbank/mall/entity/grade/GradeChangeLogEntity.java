package com.itbank.mall.entity.grade;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GradeChangeLogEntity {
    private Long logId;
    private Long memberId;
    private String previousGrade;
    private String newGrade;
    private LocalDateTime changedAt;
}