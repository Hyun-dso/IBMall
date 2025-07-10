package com.itbank.mall.service;

import com.itbank.mall.dto.OrderDetailDto;
import com.itbank.mall.entity.Order;
import com.itbank.mall.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 기존 기능 - 주문 목록
    @Override
    public List<Order> getOrdersByMemberId(long memberId) {
        return orderRepository.findOrdersByMemberId(memberId);
    }

    // 🔥 새로 추가한 기능 - 주문 상세
    @Override
    public OrderDetailDto getOrderDetail(Long orderId) {
        return orderRepository.findOrderDetailById(orderId);
    }

    // 만약 주문 등록 등 다른 메서드가 있다면 여기에 계속 붙이면 됨
}
