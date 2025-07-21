package com.itbank.mall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDto {

    private Long cartId;
    private Long memberId;
    private Long productId;
    private int quantity;

    // 조회용
    private String productName;
    private String imageUrl;
    private int price;
}
