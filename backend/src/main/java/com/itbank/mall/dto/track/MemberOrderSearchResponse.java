package com.itbank.mall.dto.track;

import java.util.List;
import lombok.Data;

/** 회원 주문 목록 응답 (페이징) */
@Data
public class MemberOrderSearchResponse {
    private List<MemberOrderSummaryDto> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
