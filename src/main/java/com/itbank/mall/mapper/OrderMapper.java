package com.itbank.mall.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.itbank.mall.dto.OrderDto;
import com.itbank.mall.entity.OrderEntity;
import com.itbank.mall.entity.OrderItemEntity;

@Mapper
public interface OrderMapper {
    // 특정 회원의 전체 주문 내역 조회
    List<OrderDto> findOrdersByMemberId(Long memberId);
    int insertOrder(OrderEntity order);
    int insertOrderItem(OrderItemEntity item);
}

