package com.itbank.mall.dto.common.email;

import lombok.Data;

@Data
public class EmailCodeVerifyRequestDto {
    private String email;
    private String code;
}