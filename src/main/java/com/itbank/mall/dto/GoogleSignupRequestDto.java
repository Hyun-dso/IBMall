package com.itbank.mall.dto;

import lombok.Data;

@Data
public class GoogleSignupRequestDto {
    private String email;
    private String nickname;
    private String password;
}
