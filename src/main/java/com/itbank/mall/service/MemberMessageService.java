package com.itbank.mall.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.MemberMessageDto;
import com.itbank.mall.entity.MemberMessageEntity;
import com.itbank.mall.mapper.MemberMessageMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberMessageService {

    private final MemberMessageMapper messageMapper;

    // 쪽지 목록 (최신순)
    public List<MemberMessageDto> getMessagesByReceiverId(long receiverId) {
        return messageMapper.selectMessagesByReceiverId(receiverId);
    }

    // 쪽지 상세 조회
    public MemberMessageEntity getMessageById(int messageId) {
        return messageMapper.selectMessageById(messageId);
    }

    // 읽음 처리
    public void markAsRead(int messageId) {
        messageMapper.updateReadStatus(messageId);
    }

    // 쪽지 전송 (관리자)
    public void sendMessage(MemberMessageEntity message) {
        messageMapper.insertMessage(message);
    }
}
