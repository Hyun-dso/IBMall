package com.itbank.mall.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.admin.grade.GradeChangeLogDto;
import com.itbank.mall.dto.admin.grade.GradeUpdateRequestDto;
import com.itbank.mall.mapper.admin.GradeChangeLogMapper;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.admin.GradeUpdateService;

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
    public ApiResponse<String> updateMemberGrade(@RequestBody GradeUpdateRequestDto dto) {
        gradeUpdateService.updateMemberGradeByMemberId(dto.getMemberId());
        return ApiResponse.ok("등급 갱신 완료");
    }
    
}

