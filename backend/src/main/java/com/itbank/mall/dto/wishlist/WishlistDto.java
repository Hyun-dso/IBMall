package com.itbank.mall.dto.wishlist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistDto {

    private Long wishlistId;
    private Long memberId;
    private Long productId;

    // 조회용 필드
    private String productName;
    private String imageUrl;
    private int price;
}
