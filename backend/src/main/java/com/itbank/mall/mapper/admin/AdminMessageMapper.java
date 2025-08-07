package com.itbank.mall.mapper.admin;

import org.apache.ibatis.annotations.Mapper;

import com.itbank.mall.dto.admin.AdminMessageDto;

@Mapper
public interface AdminMessageMapper {
    int insertAdminMessage(AdminMessageDto dto);
}
