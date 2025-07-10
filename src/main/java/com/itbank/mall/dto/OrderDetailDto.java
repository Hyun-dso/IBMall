package com.itbank.mall.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class OrderDetailDto {
    private Long orderId;
    private String orderDate;
    private String deliveryStatus;
    private int totalPrice;
    private String trackingNumber;
    private List<OrderItemDto> items;
}
