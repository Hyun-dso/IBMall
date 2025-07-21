package com.itbank.mall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 주문 상세 DTO
public class OrderItemDto {

    private String productName;  // 상품 이름
    private int quantity;        // 수량
    private int price;           // 가격

}
