package com.itbank.mall.entity.review;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Review {

    private Long id;

    private Long productId;

    private Long memberId;

    private Long paymentId;

    private Long orderItemId;

    private Integer rating;

    private String content;

    private String imageUrl;

    private Integer likes;

    private Boolean isVisible;

    private Boolean isPrivate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
