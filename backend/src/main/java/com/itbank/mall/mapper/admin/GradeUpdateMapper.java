package com.itbank.mall.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.dto.admin.grade.MemberGradeDto;
import com.itbank.mall.entity.grade.GradeRuleEntity;

@Mapper
public interface GradeUpdateMapper {
    List<MemberGradeDto> findMembersWithTotalSpent();
    List<GradeRuleEntity> findGradeRules();
    void updateMemberGrade(@Param("memberId") Long memberId,
                           @Param("grade") String grade);
    MemberGradeDto findMemberWithTotalSpentById(Long memberId);
}