package com.itbank.mall.mapper;

import com.itbank.mall.dto.AdminMessageDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMessageMapper {
    int insertAdminMessage(AdminMessageDto dto);
}
