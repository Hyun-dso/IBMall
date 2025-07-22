package com.itbank.mall.dto.common.password;

import lombok.Data;

@Data
public class PasswordResetConfirmDto {
    private String token;
    private String newPassword;
}
