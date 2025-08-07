package com.itbank.mall.service.member;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.member.MemberMessageDto;
import com.itbank.mall.mapper.member.MemberMessageMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberMessageService {

    private final MemberMessageMapper messageMapper;

    public List<MemberMessageDto> getMessagesByReceiverId(Long memberId) {
        return messageMapper.selectMessagesByReceiverId(memberId);
    }

    public MemberMessageDto getMessageById(int id) {
        MemberMessageDto dto = messageMapper.selectMessageById(id);
        System.out.println("🟡 [DEBUG] 조회된 DTO: " + dto);
        return dto;
    }

    public void markAsRead(int messageId) {
        messageMapper.updateReadStatus(messageId);
    }

    public void sendMessage(MemberMessageDto dto) {
        messageMapper.insertMessage(dto);
    }
}
