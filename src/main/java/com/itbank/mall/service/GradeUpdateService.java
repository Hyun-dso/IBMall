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

    // 🔹 전체 회원 등급 갱신 (관리자 수동용)
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
            System.out.println("회원 ID: " + member.getMemberId() +
                    ", 1년 총 사용금액: " + member.getTotalSpent() +
                    ", 갱신 등급: " + matchedGrade );

            gradeUpdateMapper.updateMemberGrade(member.getMemberId(), matchedGrade);
        }
    }

    // 🔹 개별 회원 등급 갱신 (주문 완료 시 자동)
    public void updateMemberGradeByMemberId(Long memberId) {
        MemberGradeDto member = gradeUpdateMapper.findMemberWithTotalSpentById(memberId);
        if (member == null) {
            System.out.println("❌ 회원 ID " + memberId + " 사용금액 데이터 없음");
            return;
        }

        List<GradeRuleEntity> rules = gradeUpdateMapper.findGradeRules();
        if (rules == null || rules.isEmpty()) {
            System.out.println("❌ 등급 조건(rule) 없음");
            return;
        }

        for (GradeRuleEntity rule : rules) {
            if (member.getTotalSpent() >= rule.getMinSpending()) {
                gradeUpdateMapper.updateMemberGrade(memberId, rule.getGradeName());
                System.out.println("✅ 회원 ID: " + memberId + ", 등급 갱신 → " + rule.getGradeName());
                break;
            }
        }
    }
}
