package com.itbank.mall.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemDto {
    private String productName;
    private int quantity;
    private int price;
    private String thumbnailUrl;
}
