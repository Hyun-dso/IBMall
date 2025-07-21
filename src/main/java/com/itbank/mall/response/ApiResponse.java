package com.itbank.mall.response;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String code;
    private String message;
    private T data;

    // ✅ 성공 응답 (data만)
    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setCode("SUCCESS");
        res.setMessage("정상 처리되었습니다.");
        res.setData(data);
        return res;
    }

    // ✅ 성공 응답 (data + 커스텀 메시지)
    public static <T> ApiResponse<T> ok(T data, String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setCode("SUCCESS");
        res.setMessage(message);
        res.setData(data);
        return res;
    }

    // ✅ 실패 응답
    public static <T> ApiResponse<T> fail(String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setCode("FAIL");
        res.setMessage(message);
        res.setData(null);
        return res;
    }
}
