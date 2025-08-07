package com.itbank.mall.service.admin;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.admin.AdminMessageDto;
import com.itbank.mall.mapper.admin.AdminMessageMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMessageService {

    private final AdminMessageMapper adminMessageMapper;

    public int sendMessage(AdminMessageDto dto) {
    	System.out.println("전송할 쪽지 정보: " + dto);
        return adminMessageMapper.insertAdminMessage(dto);
    }
}
