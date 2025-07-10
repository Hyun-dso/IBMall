package com.itbank.mall.repository;

import com.itbank.mall.dto.OrderDetailDto;
import com.itbank.mall.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderRepository {
    List<Order> findOrdersByMemberId(@Param("memberId") long memberId);
    OrderDetailDto findOrderDetailById(@Param("orderId") long orderId);
}
