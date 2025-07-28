package com.itbank.mall.dto.member;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberMessageDto {
    private int messageId;
    private Long receiverId;
    private String sender;
    private String title;
    private String content;
    private String imageUrl;
    private boolean isRead;
    private LocalDateTime createdAt;


@Override
public String toString() {
    return "MemberMessageDto{" +
            "messageId=" + messageId +
            ", receiverId=" + receiverId +
            ", sender='" + sender + '\'' +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", isRead=" + isRead +
            ", createdAt=" + createdAt +
            '}';
}
}