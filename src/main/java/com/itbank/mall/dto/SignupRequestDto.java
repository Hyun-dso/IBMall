package com.itbank.mall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    private String email;
    private String password;
    private String confirmPassword;
    private String name;
    private String nickname;
    private String phone;     // 선택
    private String address;   // 선택
}