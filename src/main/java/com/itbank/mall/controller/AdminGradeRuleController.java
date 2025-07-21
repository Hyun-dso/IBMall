package com.itbank.mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itbank.mall.entity.GradeRuleEntity;
import com.itbank.mall.mapper.GradeRuleMapper;

@RestController
@RequestMapping("/admin/grade-rule")
public class AdminGradeRuleController {

    @Autowired
    private GradeRuleMapper gradeRuleMapper;

    // 전체 커트라인 목록 조회 (리액트에서 화면 렌더링용)
    @GetMapping
    public ResponseEntity<?> getGradeRules() {
        return ResponseEntity.ok(gradeRuleMapper.selectAllRules());
    }

    // 등급 커트라인 수정 (JSON 요청)
    @PostMapping("/update")
    public ResponseEntity<?> updateGradeRule(@RequestBody GradeRuleEntity rule) {
        try {
            System.out.println("🔧 등급 수정됨 → " + rule.getGradeName() + " : " + rule.getMinSpending());
            gradeRuleMapper.updateRule(rule);
            return ResponseEntity.ok("등급 수정 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("등급 수정 실패");
        }
    }
}
