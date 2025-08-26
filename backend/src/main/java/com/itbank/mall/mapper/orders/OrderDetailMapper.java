package com.itbank.mall.mapper.orders;

import org.apache.ibatis.annotations.Mapper;

import com.itbank.mall.dto.orders.OrderDetailDto;

@Mapper
public interface OrderDetailMapper {

    /**
     * 특정 주문번호에 대한 상세정보 조회
     */
    OrderDetailDto findOrderDetailById(Long orderId);  // ✅ 단일 DTO

/**
 * 주문 UID 로 상세정보 조회
 */
    OrderDetailDto findOrderDetailByUid(String orderUid);

}
