package com.itbank.mall.dto.track;

import java.time.LocalDateTime;
import lombok.Data;

/** 회원 주문 목록 요약 DTO */
@Data
public class MemberOrderSummaryDto {
    private String orderUid;
    private LocalDateTime createdAt;
    private String status;
    private int totalPrice;

    // UX용 요약: "첫상품명 외 N개"
    private String itemSummary;
    private boolean hasDelivery;
}
