package com.itbank.mall.entity.admin;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

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
    private String imageUrl;		//이미지
    private LocalDateTime scheduledAt;	//예약기능
}
