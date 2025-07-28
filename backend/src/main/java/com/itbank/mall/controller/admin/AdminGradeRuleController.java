package com.itbank.mall.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.admin.grade.GradeRuleUpdateRequestDto;
import com.itbank.mall.mapper.admin.GradeRuleMapper;
import com.itbank.mall.service.admin.GradeRuleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/grade-rule")
@RequiredArgsConstructor
public class AdminGradeRuleController {

	private final GradeRuleService gradeRuleService;
    private GradeRuleMapper gradeRuleMapper;

    // 전체 커트라인 목록 조회 (리액트에서 화면 렌더링용)
    @GetMapping
    public ResponseEntity<?> getGradeRules() {
        return ResponseEntity.ok(gradeRuleMapper.selectAllRules());
    }

    // 등급 커트라인 수정 (JSON 요청)
    @PutMapping("/{gradeName}")
    public ResponseEntity<?> updateGradeRule(@PathVariable String gradeName,
                                             @RequestBody GradeRuleUpdateRequestDto dto) {
        dto.setGradeName(gradeName);
        int result = gradeRuleService.update(dto);
        return result > 0
                ? ResponseEntity.ok("등급 수정 완료")
                : ResponseEntity.badRequest().body("수정 실패");
    }
}
