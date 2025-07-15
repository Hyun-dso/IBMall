package com.itbank.mall.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmailVerification {
    private Long id;
    private String email;
    private String token;
    private LocalDateTime expiredAt;
    private Boolean isUsed;
    private LocalDateTime createdAt;
}
