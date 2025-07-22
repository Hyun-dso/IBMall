package com.itbank.mall.controller;

import com.itbank.mall.dto.AdminMessageDto;
import com.itbank.mall.service.AdminMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/message")
@RequiredArgsConstructor
public class AdminMessageController {

    private final AdminMessageService adminMessageService;

    @PostMapping("/send")
    public ResponseEntity<?> sendAdminMessage(@RequestBody AdminMessageDto dto) {
        int row = adminMessageService.sendMessage(dto);
        if (row == 1) {
            return ResponseEntity.ok("쪽지 전송 완료");
        } else {
            return ResponseEntity.badRequest().body("쪽지 전송 실패");
        }
    }
}
