package com.itbank.mall.dto.orders;

import lombok.Data;

@Data
public class GuestOrderItemDto {
    private Long productId;
    private Long productOptionId;
    private String productName;
    private int quantity;
    private int price; // 할인 적용된 개별 상품 총합
}
