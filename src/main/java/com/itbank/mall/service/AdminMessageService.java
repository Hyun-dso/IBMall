package com.itbank.mall.service;

import com.itbank.mall.dto.AdminMessageDto;
import com.itbank.mall.mapper.AdminMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMessageService {

    private final AdminMessageMapper adminMessageMapper;

    public int sendMessage(AdminMessageDto dto) {
    	System.out.println("전송할 쪽지 정보: " + dto);
        return adminMessageMapper.insertAdminMessage(dto);
    }
}
