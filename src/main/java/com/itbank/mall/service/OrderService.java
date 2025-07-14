package com.itbank.mall.service;

import com.itbank.mall.dto.OrderDto;
import com.itbank.mall.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    // 생성자 주입 (권장 방식)
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * 특정 회원의 주문 전체 내역 조회
     * @param memberId 조회할 회원 ID
     * @return 주문 내역 리스트
     */
    public List<OrderDto> getOrderListByMemberId(Long memberId) {
        return orderRepository.findOrdersByMemberId(memberId);
    }
}
