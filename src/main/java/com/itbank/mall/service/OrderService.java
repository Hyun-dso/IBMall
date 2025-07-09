package com.itbank.mall.service;

import com.itbank.mall.entity.Order;
import com.itbank.mall.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getOrdersByMemberId(Long memberId) {
        System.out.println("memberId로 주문 검색 :" + memberId);
        return orderRepository.findOrdersByMemberId(memberId);
    }

    
}

