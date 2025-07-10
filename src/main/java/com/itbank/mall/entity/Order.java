package com.itbank.mall.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Order {
    private Long orderId;
    private Long memberId;
    private String status;
    private int totalPrice;
    private String trackingNumber;
    private LocalDateTime createdAt;  // ← 변경됨
}

