package com.itbank.mall.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemEntity {
    private Long orderItemId;  // 주문 상세 ID (PK)
    private Long orderId;      // 주문 ID (FK)
    private Long productId;    // 상품 ID
    private int quantity;      // 수량
    private int price;         // 개별 항목 금액

}
