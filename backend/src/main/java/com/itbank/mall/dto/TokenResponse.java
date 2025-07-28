package com.itbank.mall.dto;

import lombok.Data;

@Data
public class TokenResponse {
    private int code;
    private String message;
    private TokenBody response;

    @Data
    public static class TokenBody {
        private String access_token;
        private long now;
        private long expired_at;
    }
}
