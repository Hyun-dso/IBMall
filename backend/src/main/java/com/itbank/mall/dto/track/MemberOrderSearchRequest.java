package com.itbank.mall.dto.track;

import java.time.LocalDate;
import lombok.Data;

/** 회원 주문 목록 검색 요청 DTO */
@Data
public class MemberOrderSearchRequest {
    private Integer page = 0;       // 0-base
    private Integer size = 10;      // 기본 10 (서비스에서 최대 50으로 제한)
    private LocalDate from;         // 옵션 (기본: 최근 90일)
    private LocalDate to;           // 옵션
    private String status = "ALL";  // ALL / ORDERED / SHIPPING / DELIVERED / CANCELLED ...
}
