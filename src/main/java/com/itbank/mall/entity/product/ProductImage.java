package com.itbank.mall.entity.product;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductImage {
    private Long imageId;           // PK
    private Long productId;         // FK
    private String imageUrl;        // 이미지 경로
    private Boolean isThumbnail;    // 썸네일 여부
    private Integer sortOrder;      // 정렬 순서
    private LocalDateTime createdAt;// 업로드 시간
}
