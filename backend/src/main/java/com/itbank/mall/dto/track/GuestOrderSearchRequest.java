package com.itbank.mall.dto.track;

import java.time.LocalDate;
import lombok.Data;

/** 비회원 주문 목록 검색 요청 (이름+전화번호) */
@Data
public class GuestOrderSearchRequest {
    private String name;          // 필수
    private String phone;         // 필수 (숫자만 비교 예정)
    private LocalDate from;       // 선택 (기본: today-90d)
    private LocalDate to;         // 선택 (기본: today)
    private Integer page = 0;     // 0-base
    private Integer size = 10;    // 기본 10, 최대 50 (서비스에서 clamp)
}
