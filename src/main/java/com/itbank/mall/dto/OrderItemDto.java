package com.itbank.mall.dto;

// 주문 상세 DTO
public class OrderItemDto {

    private String productName;  // 상품 이름
    private int quantity;        // 수량
    private int price;           // 가격

    // Getters & Setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}
