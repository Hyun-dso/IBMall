package com.itbank.mall.repository;

import com.itbank.mall.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper  // ★ 이름 넣지 마! ("orderRepository" 절대 넣지 말 것)
public interface OrderRepository {
    List<Order> findOrdersByMemberId(@Param("memberId") Long memberId);
}
