package com.itbank.mall.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminMessageDto {
    private Long receiverId;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime scheduledAt; // ← 있어도 됨

    // @Data가 자동으로 기본 생성자 + Getter/Setter 만들어줌
}
