package com.itbank.mall.dto;

import java.time.LocalDateTime;
import java.util.List;

// 주문 + 주문 상세 DTO
public class OrderDto {

    private Long orderId;                   // 주문 ID
    private int totalPrice;                 // 총 금액
    private String status;                  // 주문 상태
    private LocalDateTime createdAt;        // 주문 일시

    private List<OrderItemDto> orderItems;  // 주문 상세 목록

    // Getters & Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItemDto> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemDto> orderItems) { this.orderItems = orderItems; }
}
