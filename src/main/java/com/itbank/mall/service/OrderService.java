package com.itbank.mall.service;

import com.itbank.mall.dto.OrderDetailDto;
import com.itbank.mall.entity.Order;
import java.util.List;

public interface OrderService {
    OrderDetailDto getOrderDetail(Long orderId);
    List<Order> getOrdersByMemberId(Long memberId);
}
