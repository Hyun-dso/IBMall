package com.itbank.mall.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.admin.grade.GradeBenefitUpdateRequestDto;
import com.itbank.mall.entity.grade.GradeBenefit;
import com.itbank.mall.service.admin.GradeBenefitService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/grade-benefit")
@RequiredArgsConstructor
public class AdminGradeBenefitController {

    private final GradeBenefitService gradeBenefitService;

    // 전체 등급 혜택 조회
    @GetMapping
    public ResponseEntity<List<GradeBenefit>> findAll() {
        return ResponseEntity.ok(gradeBenefitService.getAll());
    }

    // 특정 등급 혜택 조회
    @GetMapping("/{gradeName}")
    public ResponseEntity<GradeBenefit> findByGrade(@PathVariable String gradeName) {
        return ResponseEntity.ok(gradeBenefitService.getByGrade(gradeName));
    }

    // 등급 혜택 수정
    @PutMapping("/{gradeName}")
    public ResponseEntity<?> updateBenefit(@PathVariable String gradeName,
                                           @RequestBody GradeBenefitUpdateRequestDto dto) {
        GradeBenefit entity = new GradeBenefit();
        entity.setGradeName(gradeName);
        entity.setDiscountPercent(dto.getDiscountPercent());
        entity.setMileageRate(dto.getMileageRate());

        int result = gradeBenefitService.update(entity);
        return result > 0
                ? ResponseEntity.ok("등급 혜택이 수정되었습니다.")
                : ResponseEntity.badRequest().body("수정 실패");
    }
}
