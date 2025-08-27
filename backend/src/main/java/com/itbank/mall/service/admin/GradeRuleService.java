package com.itbank.mall.service.admin;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.admin.grade.GradeRuleCreateRequestDto;
import com.itbank.mall.dto.admin.grade.GradeRuleUpdateRequestDto;
import com.itbank.mall.entity.grade.GradeRuleEntity;
import com.itbank.mall.mapper.admin.GradeRuleMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradeRuleService {

	private final GradeRuleMapper gradeRuleMapper;

    // ✅ 생성
    public int create(GradeRuleCreateRequestDto dto) {
        GradeRuleEntity e = new GradeRuleEntity();
        e.setGradeName(dto.getGradeName() == null ? null : dto.getGradeName().toUpperCase());
        e.setMinSpending(Math.max(0, dto.getMinSpending()));
        e.setDisplayOrder(dto.getDisplayOrder() == null ? 1 : Math.max(1, dto.getDisplayOrder()));
        return gradeRuleMapper.insertRule(e);
    }

    // ✅ 수정
    public int update(GradeRuleUpdateRequestDto dto) {
        GradeRuleEntity e = new GradeRuleEntity();
        e.setGradeId(dto.getGradeId()); // 필수
        e.setGradeName(dto.getGradeName() == null ? null : dto.getGradeName().toUpperCase());
        e.setMinSpending(Math.max(0, dto.getMinSpending()));
        e.setDisplayOrder(dto.getDisplayOrder() == null ? 1 : Math.max(1, dto.getDisplayOrder()));
        return gradeRuleMapper.updateRuleById(e);
    }
}
