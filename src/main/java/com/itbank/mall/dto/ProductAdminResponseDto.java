package com.itbank.mall.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductAdminResponseDto {
    private Long productId;
    private String name;
    private int price;
    private int stock;
    private Long categoryId;
    private String description;
    private String thumbnailUrl;
    private Integer viewCount;
    private Integer recommendCount;
    private Integer notRecommendCount;
    private Boolean isTimeSale;
    private Integer timeSalePrice;
    private LocalDateTime createdAt;
    private String status;
    private List<String> imageUrls;  // ← ProductImage 엔티티 없이 이미지 URL만 전달
}
