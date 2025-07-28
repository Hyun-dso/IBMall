package com.itbank.mall.dto.user.product;

import lombok.Data;

@Data
public class ProductUserResponseDto {
    private Long productId;
    private String name;
    private int price;
    private String thumbnailUrl;
    private String description;
    private Boolean isTimeSale;
    private Integer timeSalePrice;
}
