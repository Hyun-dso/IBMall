package com.itbank.mall.dto.track;

import java.time.LocalDateTime;
import lombok.Data;

/** 비회원 주문 목록 한 건 요약 */
@Data
public class GuestOrderSummaryDto {
    private String orderUid;
    private LocalDateTime createdAt;
    private String status;            // ORDERED / SHIPPING / DELIVERED ...
    private int totalPrice;

    /** 프론트 UX용: "맥북 외 1개" 등 한 줄 요약 */
    private String itemSummary;

    /** 개인정보 마스킹된 표시용 */
    private String buyerNameMasked;   // 예: 홍*동
    private String buyerPhoneMasked;  // 예: 010****5678

    private boolean hasDelivery;      // 배송 정보 존재 여부
}
