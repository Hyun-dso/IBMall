package com.itbank.mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.service.GradeUpdateService;

@RestController
@RequestMapping("/admin/grade")
public class GradeUpdateController {

    @Autowired
    private GradeUpdateService gradeUpdateService;

    @GetMapping("/update")
    public ResponseEntity<String> updateMemberGrades() {
        gradeUpdateService.updateAllMemberGrades();
        return ResponseEntity.ok("등급 갱신이 완료되었습니다.");
    }
    @PostMapping("/update/{memberId}")
    public ResponseEntity<String> updateOneMemberGrade(@PathVariable("memberId") Long memberId) {
        gradeUpdateService.updateMemberGradeByMemberId(memberId);
        return ResponseEntity.ok("회원 ID " + memberId + " 등급 갱신 완료");
    }
}
