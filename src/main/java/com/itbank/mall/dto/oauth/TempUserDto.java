package com.itbank.mall.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempUserDto {
    private String email;
    private String name;
    private String providerId;
}
