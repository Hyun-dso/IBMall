package com.itbank.mall.entity.member;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Member {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String phone;   // ex: 010-1234-5678
    private String address; // ex: 서울 강남구 ...
    private String provider;       // "local", "google", "naver"
    private String providerId;     // 소셜 로그인용 고유값 (일반 회원은 null)
    private Boolean verified;    // 이메일 인증 여부
    private String grade;
    private String role;
    private LocalDateTime createdAt;
}