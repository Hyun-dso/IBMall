package com.itbank.mall.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String email;
    private String name;
    private String nickname;
    private String phone;
    private String address;
    private String provider;
    private Boolean verified;
    private String role;
    private String grade;              // ✅ 추가
    private LocalDateTime createdAt;
}
