package com.itbank.mall.controller.admin;

import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.admin.GradeUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/grade")
@RequiredArgsConstructor
public class GradeUpdateController {

    private final GradeUpdateService gradeUpdateService;

    // ✅ 전체 회원 등급 갱신
    @GetMapping("/update")
    public ApiResponse<String> updateMemberGrades() {
        gradeUpdateService.updateAllMemberGrades();
        return ApiResponse.ok(null, "전체 회원 등급 갱신 완료");
    }

    // ✅ 특정 회원 등급 갱신
    @PostMapping("/update/{memberId}")
    public ApiResponse<String> updateOneMemberGrade(@PathVariable("memberId") Long memberId) {
        gradeUpdateService.updateMemberGradeByMemberId(memberId);
        return ApiResponse.ok(null, "회원 ID " + memberId + " 등급 갱신 완료");
    }
}
