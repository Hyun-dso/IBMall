package com.itbank.mall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminMessageDto {
    private Long receiverId;   // 수신자 ID
    private String title;      // 제목
    private String content;    // 내용
}
