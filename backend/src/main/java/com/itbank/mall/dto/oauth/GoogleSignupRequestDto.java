package com.itbank.mall.dto.oauth;

import lombok.Data;

@Data
public class GoogleSignupRequestDto {
    private String email;
    private String nickname;
    private String password;
    private String confirmPassword;
}
