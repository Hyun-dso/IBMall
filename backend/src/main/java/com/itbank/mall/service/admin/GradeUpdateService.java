package com.itbank.mall.service.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.admin.grade.GradeChangeLogDto;
import com.itbank.mall.dto.admin.grade.MemberGradeDto;
import com.itbank.mall.entity.grade.GradeRuleEntity;
import com.itbank.mall.mapper.admin.GradeChangeLogMapper;
import com.itbank.mall.mapper.admin.GradeUpdateMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradeUpdateService {

	private final GradeUpdateMapper gradeUpdateMapper;
	private final GradeChangeLogMapper gradeChangeLogMapper;

	// ✅ 1. 공통 메서드: 등급 변경 + 로그 기록
	private void updateAndLog(Long memberId, String previousGrade, String newGrade) {
		if (!Objects.equals(previousGrade, newGrade)) {
			gradeUpdateMapper.updateMemberGrade(memberId, newGrade);

			GradeChangeLogDto log = new GradeChangeLogDto();
			log.setMemberId(memberId);
			log.setPreviousGrade(previousGrade);
			log.setNewGrade(newGrade);
			log.setChangedAt(LocalDateTime.now());

			gradeChangeLogMapper.insertGradeChangeLog(log);
			System.out.println("✅ 등급 갱신 및 로그 기록 완료: " + previousGrade + " → " + newGrade);
		} else {
			System.out.println("⏩ 등급 동일: 변경/기록 생략");
		}
	}

	// ✅ 2. 전체 회원 등급 갱신 (관리자 수동)
	public void updateAllMemberGrades() {
		List<MemberGradeDto> members = gradeUpdateMapper.findMembersWithTotalSpent();
		List<GradeRuleEntity> rules = gradeUpdateMapper.findGradeRules();

		for (MemberGradeDto member : members) {
			int spent = member.getTotalSpent();
			String previousGrade = member.getMatchedGrade();
			String matchedGrade = "basic";

			for (GradeRuleEntity rule : rules) {
				if (spent >= rule.getMinSpending()) {
					matchedGrade = rule.getGradeName();
					break;
				}
			}

			System.out.println("🧾 회원 ID: " + member.getMemberId() + ", 사용금액: " + spent + ", 기존 등급: " + previousGrade
					+ ", 갱신 등급: " + matchedGrade);

			updateAndLog(member.getMemberId(), previousGrade, matchedGrade);
		}
	}

	// ✅ 3. 개별 회원 등급 갱신 (자동)
	public void updateMemberGradeByMemberId(Long memberId) {
		MemberGradeDto member = gradeUpdateMapper.findMemberWithTotalSpentById(memberId);
		if (member == null) {
			System.out.println("❌ 회원 ID " + memberId + " 사용금액 정보 없음");
			return;
		}

		List<GradeRuleEntity> rules = gradeUpdateMapper.findGradeRules();
		if (rules == null || rules.isEmpty()) {
			System.out.println("❌ 등급 조건(rule) 없음");
			return;
		}

		String previousGrade = member.getMatchedGrade();
		String newGrade = "basic";

		for (GradeRuleEntity rule : rules) {
			if (member.getTotalSpent() >= rule.getMinSpending()) {
				newGrade = rule.getGradeName();
				break;
			}
		}

		updateAndLog(memberId, previousGrade, newGrade);
	}

	// ✅ 등급 변경 이력 전체 조회
	public List<GradeChangeLogDto> getGradeChangeLogs() {
		return gradeChangeLogMapper.selectGradeChangeLogs();
	}
}
