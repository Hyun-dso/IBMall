package com.itbank.mall.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itbank.mall.dto.AdminMessageDto;
import com.itbank.mall.service.AdminMessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/message")
@RequiredArgsConstructor
public class AdminMessageController {

    private final AdminMessageService adminMessageService;
    private final ObjectMapper objectMapper;

    @PostMapping("/send")
    public ResponseEntity<?> sendAdminMessage(@RequestBody AdminMessageDto dto) {
        int row = adminMessageService.sendMessage(dto);
        return row == 1
            ? ResponseEntity.ok("쪽지 전송 완료")
            : ResponseEntity.badRequest().body("쪽지 전송 실패");
    }

}
