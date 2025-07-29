package com.itbank.mall.dto.orders;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 주문 + 주문 상세 DTO
public class OrderDto {

    private Long orderId;                   // 주문 ID
    private int totalPrice;                 // 총 금액
    private String status;                  // 주문 상태
    private LocalDateTime createdAt;        // 주문 일시
    private List<OrderItemDto> orderItems;  // 주문 상세 목록
}