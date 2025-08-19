package com.itbank.mall.entity.orders;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemEntity {
    private Long orderItemId;   // PK
    private Long orderId;       // FK
    private Long productId;     // 상품 ID
    private Long productOptionId; // ✅ 추가: 옵션 ID (NULL 허용)
    private int quantity;       // 수량
    private int price;          // 단가(unit price)
}
