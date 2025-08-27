package com.itbank.mall.controller.admin;

import com.itbank.mall.dto.admin.grade.GradeBenefitUpdateRequestDto;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.admin.GradeBenefitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/grade-benefits")
@RequiredArgsConstructor
public class GradeBenefitController {

    private final GradeBenefitService gradeBenefitService;

    // ✅ 혜택 수정 (등급명은 PathVar로 식별, 대소문자 무시)
    @PutMapping("/{gradeName}")
    public ApiResponse<Void> updateBenefit(@PathVariable String gradeName,
                                           @RequestBody GradeBenefitUpdateRequestDto body) {
        gradeBenefitService.updateByGradeName(gradeName, body);
        return ApiResponse.ok(null, "혜택 수정 완료(" + gradeName.toUpperCase() + ")");
    }
}
