package com.itbank.mall.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.itbank.mall.dto.MemberMessageDto;

@Mapper
public interface MemberMessageMapper {

    List<MemberMessageDto> selectMessagesByReceiverId(Long receiverId);

    MemberMessageDto selectMessageById(int messageId);  // ✅ DTO로 반환 통일

    void updateReadStatus(int messageId);

    void insertMessage(MemberMessageDto dto);  // insert도 DTO로 변경 가능
}