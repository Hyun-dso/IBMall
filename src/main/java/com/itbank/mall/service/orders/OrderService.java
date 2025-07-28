package com.itbank.mall.service.orders;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.dto.orders.OrderDto;
import com.itbank.mall.entity.orders.OrderEntity;
import com.itbank.mall.entity.orders.OrderItemEntity;
import com.itbank.mall.mapper.orders.OrderMapper;

@Service
public class OrderService {

    private final OrderMapper orderMapper;

    public OrderService(OrderMapper orderRepository) {
        this.orderMapper = orderRepository;
    }

    public List<OrderDto> getOrderListByMemberId(Long memberId) {
        return orderMapper.findOrdersByMemberId(memberId);
    }

    @Transactional
    public Long createOrder(OrderEntity order) {
        order.setCreatedAt(LocalDateTime.now());

        if (order.getStatus() == null) {
            order.setStatus("주문접수");
        }

        if (order.getOrderType() == null) {
            order.setOrderType(order.getMemberId() != null ? "MEMBER" : "GUEST");
        }

        orderMapper.insertOrder(order);

        for (OrderItemEntity item : order.getOrderItems()) {
            item.setOrderId(order.getOrderId());
            orderMapper.insertOrderItem(item);
        }

        return order.getOrderId();
    }
}
