package com.itbank.mall.service.admin;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.admin.grade.GradeRuleUpdateRequestDto;
import com.itbank.mall.entity.grade.GradeRuleEntity;
import com.itbank.mall.mapper.admin.GradeRuleMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradeRuleService {

    private final GradeRuleMapper gradeRuleMapper;

    public int update(GradeRuleUpdateRequestDto dto) {
        GradeRuleEntity entity = new GradeRuleEntity();
        entity.setGradeName(dto.getGradeName());
        entity.setMinSpending(dto.getMinSpending());
        return gradeRuleMapper.updateRuleById(entity);
    }
}
