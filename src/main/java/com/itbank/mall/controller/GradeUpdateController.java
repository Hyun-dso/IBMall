package com.itbank.mall.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.service.GradeUpdateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class GradeUpdateController {

    private final GradeUpdateService gradeUpdateService;

    @PostMapping("/update-grade")
    public String updateMemberGradesManually() {
        gradeUpdateService.updateAllMemberGrades();
        return "회원 등급이 수동으로 갱신되었습니다.";
    }
}
