package com.itbank.mall.mapper;

import com.itbank.mall.entity.MemberMessageEntity;
import com.itbank.mall.dto.MemberMessageDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMessageMapper {

    // 쪽지 목록 (최신순)
    List<MemberMessageDto> selectMessagesByReceiverId(long receiverId);

    // 쪽지 상세
    MemberMessageEntity selectMessageById(int messageId);

    // 읽음 처리
    int updateReadStatus(int messageId);

    // 쪽지 등록
    int insertMessage(MemberMessageEntity message);
}
