package com.itbank.mall.service;

import com.itbank.mall.dto.OrderDetailDto;
import com.itbank.mall.dto.OrderItemDto;
import com.itbank.mall.entity.Order;
import com.itbank.mall.entity.OrderItem;
import com.itbank.mall.repository.OrderRepository;
import com.itbank.mall.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<Order> getOrdersByMemberId(Long memberId) {
        System.out.println("memberId=" + memberId + " 주문 검색");
        return orderRepository.findOrdersByMemberId(memberId);
    }

    @Override
    public OrderDetailDto getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다"));

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        List<OrderItemDto> itemDtos = items.stream()
                .map(item -> OrderItemDto.builder()
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .thumbnailUrl(item.getProduct().getThumbnailUrl())
                        .build())
                .collect(Collectors.toList());

        return OrderDetailDto.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate().toString())
                .deliveryStatus(order.getDeliveryStatus())
                .totalPrice(order.getTotalPrice())
                .trackingNumber(order.getTrackingNumber())
                .items(itemDtos)
                .build();
    }
}
