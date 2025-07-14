package com.itbank.mall.entity;

public class OrderItemEntity {
    private Long orderItemId;  // 주문 상세 ID (PK)
    private Long orderId;      // 주문 ID (FK)
    private Long productId;    // 상품 ID
    private int quantity;      // 수량
    private int price;         // 개별 항목 금액

    // Getters & Setters
    public Long getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}
