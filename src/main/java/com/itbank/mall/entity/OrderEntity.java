package com.itbank.mall.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderEntity {
    private Long orderId;         // 주문 ID (PK)
    private Long memberId;        // 주문자 회원 ID
    private int totalPrice;       // 주문 총 금액
    private String status;        // 주문 상태 (ex. 주문접수, 배송중, 완료 등)
    private LocalDateTime createdAt;  // 주문 생성 시간
    private String buyerPhone;
    private String buyerAddress;
    private String orderType; // ex. "MEMBER" or "GUEST"
    private List<OrderItemEntity> orderItems; // 주문 상품 목록

}
