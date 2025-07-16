package com.itbank.mall.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.MemberGradeDto;
import com.itbank.mall.entity.GradeRuleEntity;
import com.itbank.mall.mapper.GradeUpdateMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradeUpdateService {

    private final GradeUpdateMapper gradeUpdateMapper;

    public void updateAllMemberGrades() {
        List<MemberGradeDto> members = gradeUpdateMapper.findMembersWithTotalSpent();
        List<GradeRuleEntity> rules = gradeUpdateMapper.findGradeRules();

        for (MemberGradeDto member : members) {
            int spent = member.getTotalSpent();
            String matchedGrade = "basic";

            for (GradeRuleEntity rule : rules) {
                if (spent >= rule.getMinSpending()) {
                    matchedGrade = rule.getGradeName();
                    break;
                }
            }
            gradeUpdateMapper.updateMemberGrade(member.getMemberId(), matchedGrade);
        }
    }
}
