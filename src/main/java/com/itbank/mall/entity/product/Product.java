package com.itbank.mall.entity.product;

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
    private Integer viewCount;
    private Integer recommendCount;
    private Integer notRecommendCount;
    private Boolean isTimeSale;
    private Integer timeSalePrice;
    private LocalDateTime createdAt;
   
    // ✅ 추가: 상품 노출 상태
    private String status;

    // ✅ 추가: 노출 여부 판단 메서드
    public boolean isVisible() {
        return "ACTIVE".equalsIgnoreCase(this.status);
    }
}
