package com.itbank.mall.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.dto.OrderDetailDto;

@Mapper
public interface OrderMapper {
    OrderDetailDto findOrderDetailById(@Param("orderId") Long orderId);
}
