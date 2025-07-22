package com.itbank.mall.controller;

import com.itbank.mall.entity.MemberMessageEntity;
import com.itbank.mall.service.MemberMessageService;
import com.itbank.mall.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MemberMessageController {

    private final MemberMessageService messageService;
    private final JwtUtil jwtUtil;

    // 🔹 쪽지 목록 조회
    @GetMapping
    public List<?> getMessages(HttpServletRequest request) {
        Long memberId = extractMemberId(request);
        return messageService.getMessagesByReceiverId(memberId);
    }

    // 🔹 쪽지 상세 조회
    @GetMapping("/{id}")
    public MemberMessageEntity getMessage(@PathVariable("id") int messageId,
                                    HttpServletRequest request) {
        Long memberId = extractMemberId(request);
        MemberMessageEntity msg = messageService.getMessageById(messageId);

        if (msg != null && memberId != null && memberId.equals(msg.getReceiverId())) {
            messageService.markAsRead(messageId);
            return msg;
        }

        return null;  // or throw new ResponseStatusException(HttpStatus.FORBIDDEN)
    }

    // 🔸 토큰에서 memberId 추출
    private Long extractMemberId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("JWT 토큰이 누락되었거나 잘못되었습니다");
        }
        String token = authHeader.substring(7); // "Bearer " 제거
        return jwtUtil.getIdFromToken(token);
    }
}
