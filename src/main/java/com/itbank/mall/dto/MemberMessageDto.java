package com.itbank.mall.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class MemberMessageDto {
    private int messageId;
    private String sender;
    private String title;
    private boolean isRead;
    private LocalDateTime createdAt;
}
