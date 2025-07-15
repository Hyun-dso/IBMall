package com.itbank.mall.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long productId;
    private String name;
    private int price;
    private int stock;
    private Long categoryId;
    private String description;
    private String thumbnailUrl;
    private int viewCount;
    private int recommendCount;
    private int notRecommendCount;
    private boolean isTimeSale;
    private Integer timeSalePrice;
    private LocalDateTime createdAt;
}
