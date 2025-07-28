package com.itbank.mall.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.entity.GradeRuleEntity;

@Mapper
public interface GradeRuleMapper {
    List<GradeRuleEntity> selectAllRules();
    int insertRule(GradeRuleEntity gradeRule);
    int updateRuleById(GradeRuleEntity rule);       // 🔄 수정 메서드명 변경
    int deleteRuleById(@Param("gradeId") Long gradeId);  // 🔄 삭제도 gradeId 기준
}
