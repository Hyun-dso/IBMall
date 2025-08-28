package com.itbank.mall.service.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.dto.admin.grade.GradeChangeLogDto;
import com.itbank.mall.dto.admin.grade.MemberGradeDto;
import com.itbank.mall.entity.grade.GradeRuleEntity;
import com.itbank.mall.mapper.admin.GradeChangeLogMapper;
import com.itbank.mall.mapper.admin.GradeUpdateMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradeUpdateService {

	private static final String DEFAULT_GRADE = "BASIC";
	
	private final GradeUpdateMapper gradeUpdateMapper;
	private final GradeChangeLogMapper gradeChangeLogMapper;
	
    @Getter
    public static class BulkResult {
        private final int updated;
        private final int skipped;
        public BulkResult(int updated, int skipped) {
            this.updated = updated;
            this.skipped = skipped;
        }
    }

	// ✅ 1. 공통 메서드: 등급 변경 + 로그 기록
    // ✅ 내부 공통: 등급 변경 + 로그 (dryRun 지원, reason 전달)
    private boolean updateAndLog(Long memberId, String previousGrade, String newGrade, String reason, boolean dryRun) {
        String prev = previousGrade == null ? DEFAULT_GRADE : previousGrade.toUpperCase();
        String next = (newGrade == null ? DEFAULT_GRADE : newGrade.toUpperCase());

        if (Objects.equals(prev, next)) {
            return false; // 스킵
        }
        if (!dryRun) {
            gradeUpdateMapper.updateMemberGrade(memberId, next);

            GradeChangeLogDto log = new GradeChangeLogDto();
            log.setMemberId(memberId);
            log.setPreviousGrade(prev);
            log.setNewGrade(next);
            log.setChangedAt(LocalDateTime.now());
            log.setReason(reason);
            gradeChangeLogMapper.insertGradeChangeLog(log);
        }
        return true; // 업데이트 발생
    }


	// ✅ 2. 전체 회원 등급 갱신 (관리자 수동)
    @Transactional
    public BulkResult updateAllMemberGrades(String reason, boolean dryRun) {
        List<MemberGradeDto> members = gradeUpdateMapper.findMembersWithTotalSpent();
        List<GradeRuleEntity> rules = gradeUpdateMapper.findGradeRules();

        int updated = 0, skipped = 0;

        for (MemberGradeDto member : members) {
            int spent = member.getTotalSpent();
            String previousGrade = (member.getMatchedGrade() == null ? DEFAULT_GRADE : member.getMatchedGrade().toUpperCase());
            String matchedGrade = DEFAULT_GRADE;

            for (GradeRuleEntity rule : rules) {
                String ruleName = rule.getGradeName() == null ? DEFAULT_GRADE : rule.getGradeName().toUpperCase();
                if (spent >= rule.getMinSpending()) {
                    matchedGrade = ruleName;
                    break;
                }
            }

            boolean changed = updateAndLog(member.getMemberId(), previousGrade, matchedGrade, reason, dryRun);
            if (changed) updated++; else skipped++;
        }
        return new BulkResult(updated, skipped);
    }

	// ✅ 3. 개별 회원 등급 갱신 (reason/dryRun 지원)
    @Transactional
    public boolean updateMemberGradeByMemberId(Long memberId, String reason, boolean dryRun) {
        MemberGradeDto member = gradeUpdateMapper.findMemberWithTotalSpentById(memberId);
        if (member == null) {
            return false;
        }
        List<GradeRuleEntity> rules = gradeUpdateMapper.findGradeRules();
        if (rules == null || rules.isEmpty()) {
            return false;
        }

        String previousGrade = (member.getMatchedGrade() == null ? DEFAULT_GRADE : member.getMatchedGrade().toUpperCase());
        String newGrade = DEFAULT_GRADE;

        for (GradeRuleEntity rule : rules) {
            String ruleName = rule.getGradeName() == null ? DEFAULT_GRADE : rule.getGradeName().toUpperCase();
            if (member.getTotalSpent() >= rule.getMinSpending()) {
                newGrade = ruleName;
                break;
            }
        }
        return updateAndLog(memberId, previousGrade, newGrade, reason, dryRun);
    }
    
    @Transactional
    public boolean updateMemberGradeByMemberId(Long memberId) {
    	return updateMemberGradeByMemberId(memberId, "ADMIN_SINGLE_UPDATE", false);
    }

    // ✅ 변경 이력 조회(기존)
    public List<GradeChangeLogDto> getGradeChangeLogs() {
        return gradeChangeLogMapper.selectGradeChangeLogs();
    }
}
