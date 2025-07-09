package com.itbank.mall.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Order {
    private Long orderId;
    private Long memberId;
    private LocalDateTime orderDate;
    private String status;
    private int totalAmount;
}
