package com.itbank.mall.dto.orders;

import lombok.Data;

@Data
public class GuestOrderItemDto {
    private Long productId;
    private Long productOptionId;
    private String productName;
}
