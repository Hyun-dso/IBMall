package com.itbank.mall.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminMessageEntity {
    private int messageId;
    private Long receiverId;
    private String sender;         // 기본값은 'admin'
    private String title;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;
}
