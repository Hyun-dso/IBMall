package com.itbank.mall.dto.track;

import java.util.List;
import lombok.Data;

/** 비회원 주문 목록 검색 응답 (페이징) */
@Data
public class GuestOrderSearchResponse {
    private List<GuestOrderSummaryDto> content;
    private int page;            // 요청 page 그대로 반환
    private int size;            // 실제 size
    private long totalElements;
    private int totalPages;
}
