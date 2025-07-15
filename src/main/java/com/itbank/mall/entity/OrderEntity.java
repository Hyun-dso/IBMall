package com.itbank.mall.entity;

import java.time.LocalDateTime;
import java.util.List;

public class OrderEntity {
    private Long orderId;         // 주문 ID (PK)
    private Long memberId;        // 주문자 회원 ID
    private int totalPrice;       // 주문 총 금액
    private String status;        // 주문 상태 (ex. 주문접수, 배송중, 완료 등)
    private LocalDateTime createdAt;  // 주문 생성 시간

    private List<OrderItemEntity> orderItems; // 주문 상품 목록

    // Getters & Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItemEntity> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemEntity> orderItems) { this.orderItems = orderItems; }
}
