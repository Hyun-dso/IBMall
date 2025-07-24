package com.itbank.mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
