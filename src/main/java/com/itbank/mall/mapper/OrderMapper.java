package com.itbank.mall.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.itbank.mall.dto.OrderDto;
import java.util.List;

@Mapper
public interface OrderMapper {
    
    // 특정 회원의 전체 주문 내역 조회
    List<OrderDto> findOrdersByMemberId(Long memberId);
}

