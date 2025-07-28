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

    // ✅ 타임세일 기간 필드 추가
    private LocalDateTime timeSaleStart;
    private LocalDateTime timeSaleEnd;

    // ✅ 추가: 상품 노출 상태
    private String status;

    // ✅ 추가: 노출 여부 판단 메서드
    public boolean isVisible() {
        return "ACTIVE".equalsIgnoreCase(this.status);
    }

    // ✅ 현재 가격 계산 (타임세일 조건에 따라)
    public int getEffectivePrice(LocalDateTime now) {
        if (Boolean.TRUE.equals(isTimeSale)
                && timeSalePrice != null
                && timeSaleStart != null && timeSaleEnd != null
                && now.isAfter(timeSaleStart)
                && now.isBefore(timeSaleEnd)) {
            return timeSalePrice;
        }
        return price;
    }
}
