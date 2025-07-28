package com.itbank.mall.entity.product;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductImage {
    private Long imageId;           // 이미지 ID (PK)
    private Long productId;         // 상품 ID (FK)
    private String imageUrl;        // 이미지 파일 URL
    private Integer sortOrder;      // 정렬 순서
    private LocalDateTime createdAt;// 생성일자 (업로드 시각)
}
