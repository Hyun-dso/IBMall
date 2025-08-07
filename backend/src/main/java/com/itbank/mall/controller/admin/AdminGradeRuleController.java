package com.itbank.mall.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itbank.mall.entity.grade.GradeRuleEntity;
import com.itbank.mall.mapper.admin.GradeRuleMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/grade-rule")
@RequiredArgsConstructor
public class AdminGradeRuleController {

    private final GradeRuleMapper gradeRuleMapper;

    // ✅ 전체 등급 목록 조회
    @GetMapping
    public ResponseEntity<List<GradeRuleEntity>> getGradeRules() {
        return ResponseEntity.ok(gradeRuleMapper.selectAllRules());
    }

    // ✅ 등급 추가
    @PostMapping("/add")
    public ResponseEntity<String> addGrade(@RequestBody GradeRuleEntity newGrade) {
        int result = gradeRuleMapper.insertRule(newGrade);
        return result > 0
                ? ResponseEntity.ok("등급 추가 완료")
                : ResponseEntity.badRequest().body("등급 추가 실패");
    }

    // ✅ 등급 삭제 (gradeId 기준)
    @DeleteMapping("/delete/{gradeId}")
    public ResponseEntity<String> deleteGrade(@PathVariable("gradeId") Long gradeId) {
        System.out.println("🔥 컨트롤러 진입! gradeId = " + gradeId);
        int result = gradeRuleMapper.deleteRuleById(gradeId);
        return result > 0
                ? ResponseEntity.ok("등급 삭제 완료")
                : ResponseEntity.badRequest().body("등급 삭제 실패");
    }

    // ✅ 등급 수정 (gradeId 기준)
    @PostMapping("/update")
    public ResponseEntity<String> updateGrade(@RequestBody GradeRuleEntity updatedGrade) {
        int result = gradeRuleMapper.updateRuleById(updatedGrade);
        return result > 0
                ? ResponseEntity.ok("등급 수정 완료")
                : ResponseEntity.badRequest().body("등급 수정 실패");
    }

    // 🔁 (참고용) 기존 Multipart 테스트 코드
    @PostMapping("/send")
    public ResponseEntity<?> testMultipart(
        @RequestParam("receiverId") Long receiverId,
        @RequestParam("title") String title,
        @RequestParam("content") String content,
        @RequestParam(value = "image_url", required = false) MultipartFile image
    ) {
        System.out.println("✅ title: " + title);
        System.out.println("✅ image: " + (image != null ? image.getOriginalFilename() : "null"));
        return ResponseEntity.ok("성공!");
    }
}
