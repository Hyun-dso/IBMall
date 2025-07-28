package com.itbank.mall.entity.auth;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PasswordResetToken {
    private int id;
    private String email;
    private String token;
    private LocalDateTime expiredAt;
    private Boolean isUsed;
    private LocalDateTime createdAt;
}
