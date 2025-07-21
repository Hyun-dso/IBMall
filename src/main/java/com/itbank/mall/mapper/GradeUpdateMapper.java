package com.itbank.mall.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.dto.MemberGradeDto;
import com.itbank.mall.entity.GradeRuleEntity;

@Mapper
public interface GradeUpdateMapper {
    List<MemberGradeDto> findMembersWithTotalSpent();
    List<GradeRuleEntity> findGradeRules();
    void updateMemberGrade(@Param("memberId") Long memberId,
                           @Param("grade") String grade);
    MemberGradeDto findMemberWithTotalSpentById(Long memberId);
}