package com.itbank.mall.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistEntity {

    private Long wishlistId;
    private Long memberId;
    private Long productId;
}
