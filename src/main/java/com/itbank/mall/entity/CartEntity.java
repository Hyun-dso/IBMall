package com.itbank.mall.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartEntity {

    private Long cartId;
    private Long memberId;
    private Long productId;
    private int quantity;
}
