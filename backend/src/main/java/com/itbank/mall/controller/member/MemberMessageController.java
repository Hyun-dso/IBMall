package com.itbank.mall.controller.member;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.itbank.mall.dto.member.MemberMessageDto;
import com.itbank.mall.service.member.MemberMessageService;
import com.itbank.mall.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MemberMessageController {

    private final MemberMessageService messageService;
    private final JwtUtil jwtUtil;

    // 🔹 쪽지 목록 조회
    @GetMapping
    public List<MemberMessageDto> getMessages(HttpServletRequest request) {
        Long memberId = extractMemberId(request);
        return messageService.getMessagesByReceiverId(memberId);
    }

    // 🔹 쪽지 상세 조회
    @GetMapping("/{id}")
    public MemberMessageDto getMessage(@PathVariable("id") int messageId,
                                       HttpServletRequest request) {
        Long memberId = extractMemberId(request);
        MemberMessageDto dto = messageService.getMessageById(messageId);

        if (dto != null && memberId != null && memberId.equals(dto.getReceiverId())) {
            messageService.markAsRead(messageId);
            return dto;
        }

        return null;
    }

    private Long extractMemberId(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT 토큰이 없거나 유효하지 않습니다.");
        }
        return jwtUtil.getMemberId(token); // ★ getIdFromToken() 대신 getMemberId()
    }
}