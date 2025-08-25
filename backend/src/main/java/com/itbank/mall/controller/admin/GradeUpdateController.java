package com.itbank.mall.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.itbank.mall.dto.admin.grade.GradeBulkUpdateRequest;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.admin.GradeUpdateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/grade")
@RequiredArgsConstructor
public class GradeUpdateController {

    private final GradeUpdateService gradeUpdateService;

    // ✅ 전체 회원 등급 갱신: GET → POST 전환 + 옵션 바디(reason, dryRun)
    @PostMapping("/update")
    public ApiResponse<String> updateMemberGrades(@RequestBody(required = false) GradeBulkUpdateRequest req) {
        String reason = (req != null && req.getReason() != null) ? req.getReason() : "ADMIN_BULK_UPDATE";
        boolean dryRun = (req != null && Boolean.TRUE.equals(req.getDryRun()));

        var result = gradeUpdateService.updateAllMemberGrades(reason, dryRun);
        String msg = String.format("전체 회원 등급 갱신 완료 (updated=%d, skipped=%d, dryRun=%s)",
                result.getUpdated(), result.getSkipped(), dryRun);
        return ApiResponse.ok(null, msg);
    }
    
    @GetMapping("/update")
    public void deprecatedGetUpdate() {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED,
                "이 API는 GET을 지원하지 않습니다. POST /api/admin/grade/update 사용하세요.");
    }

    // ✅ 특정 회원 등급 갱신 (기존 POST 유지, reason 전달 지원)
    @PostMapping("/update/{memberId}")
    public ApiResponse<String> updateOneMemberGrade(@PathVariable("memberId") Long memberId,
                                                    @RequestParam(value = "reason", required = false) String reason) {
        String r = (reason != null ? reason : "ADMIN_SINGLE_UPDATE");
        boolean updated = gradeUpdateService.updateMemberGradeByMemberId(memberId, r, false);
        String msg = updated ? ("회원 ID " + memberId + " 등급 갱신 완료")
                             : ("회원 ID " + memberId + " 등급 변경 없음");
        return ApiResponse.ok(null, msg);
    }
}
