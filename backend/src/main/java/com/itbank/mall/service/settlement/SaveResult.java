package com.itbank.mall.service.settlement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SaveResult {
    public enum Status {
        SUCCESS,
        INVALID_TYPE,            // type != actual
        PAYMENT_NOT_PAID,        // 결제 PAID 없음
        DELIVERY_NOT_DELIVERED,  // 배송 DELIVERED 아님
        AMOUNT_MISMATCH,         // 금액 정합성 불일치
        INSERT_FAILED            // DB insert 실패
    }
    private final Status status;
    private final Long id;      // insert PK 필요시 (이번엔 null 가능)
    private final String reason; // 상세 사유(로그/응답 메시지)
}
