package com.itbank.mall.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.itbank.mall.dto.OrderDetailDto;

@Mapper
public interface OrderDetailMapper {

    /**
     * 특정 주문번호에 대한 상세정보 조회
     */
	OrderDetailDto findOrderDetailById(Long orderId);  // ✅ 단일 DTO

}
