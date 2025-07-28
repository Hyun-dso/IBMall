package com.itbank.mall.dto.admin.grade;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GradeChangeLogDto {
    private Long memberId;
    private String previousGrade;
    private String newGrade;
    private LocalDateTime changedAt;
}