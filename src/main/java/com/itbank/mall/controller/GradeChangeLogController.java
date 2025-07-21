package com.itbank.mall.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.GradeChangeLogDto;
import com.itbank.mall.mapper.GradeChangeLogMapper;
import com.itbank.mall.service.GradeUpdateService;

@RestController
@RequestMapping("/admin/grade-change-log")
public class GradeChangeLogController {

    private final GradeChangeLogMapper gradeChangeLogMapper;
    private final GradeUpdateService gradeUpdateService;
    
    public GradeChangeLogController(
    	    GradeChangeLogMapper gradeChangeLogMapper,
    	    GradeUpdateService gradeUpdateService   // ✅ 이거 추가!
    	) {
    	    this.gradeChangeLogMapper = gradeChangeLogMapper;
    	    this.gradeUpdateService = gradeUpdateService;  // ✅ 초기화
    	}

    @GetMapping
    public List<GradeChangeLogDto> getGradeChangeLogs() {
        return gradeChangeLogMapper.selectGradeChangeLogs();
    }
    // 🔹 등급 갱신 수동 호출 (Postman 테스트용)
    @GetMapping("/update-member-grade")
    public void updateMemberGrade(Long memberId) {
        gradeUpdateService.updateMemberGradeByMemberId(memberId);
    }
    @PostMapping("/update-member-grade")
    public void updateMemberGrade(@RequestBody Map<String, Long> body) {
        Long memberId = body.get("memberId");
        gradeUpdateService.updateMemberGradeByMemberId(memberId);
    }
    
}

