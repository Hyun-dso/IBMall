package com.itbank.mall.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductImage {
    private Long imageId;       // PK: 이미지 고유번호
    private Long productId;     // FK: 연결된 상품 ID
    private String imageUrl;    // 이미지 파일 경로 (예: /upload/productImg/uuid_filename.jpg)
    private LocalDateTime createdAt;  // 업로드된 시간
}