package com.itbank.mall.entity.member;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberMessageEntity {
    private int messageId;
    private Long receiverId;         // member.id 참조
    private String sender;           // 기본값: "admin"
    private String title;
    private String content;
    private boolean isRead;          // 0 / 1 → boolean 처리
    private LocalDateTime createdAt;
    private String imageUrl;		//이미지
}
