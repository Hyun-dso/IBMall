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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/grade/logs") // ✅ 베이스 경로 통일
@RequiredArgsConstructor                 // ✅ 수동 생성자 제거
public class GradeChangeLogController {

    private final GradeChangeLogMapper gradeChangeLogMapper;
    private final GradeUpdateService gradeUpdateService;

    // ✅ 목록도 ApiResponse로 통일
    @GetMapping(produces = "application/json")
    public ApiResponse<List<GradeChangeLogDto>> getGradeChangeLogs() {
        return ApiResponse.ok(gradeChangeLogMapper.selectGradeChangeLogs(), "OK");
    }
    // ✅ 상태 변경은 POST만 유지
    @PostMapping(value = "/update-member-grade", consumes = "application/json", produces = "application/json")
    public ApiResponse<String> updateMemberGrade(@RequestBody GradeUpdateRequestDto dto) {
        boolean updated = gradeUpdateService.updateMemberGradeByMemberId(dto.getMemberId());
        return ApiResponse.ok(updated ? "등급 갱신 완료" : "등급 변경 없음");
    }
}

