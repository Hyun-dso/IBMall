package com.itbank.mall.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.itbank.mall.service.GradeUpdateService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GradeUpdateScheduler {

    private final GradeUpdateService gradeUpdateService;

    // 매일 자정에 실행 (테스트용으로는 10초마다로 바꿔도 됨)
    @Scheduled(cron = "0 0 0 * * *") // 매일 00:00:00
    public void scheduledMemberGradeUpdate() {
        gradeUpdateService.updateAllMemberGrades();
        System.out.println("자동 등급 갱신 완료됨 ✅");
    }
}
