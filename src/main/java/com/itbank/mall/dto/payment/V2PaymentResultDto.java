package com.itbank.mall.dto.payment;

import lombok.Data;

@Data
public class V2PaymentResultDto {

    private String id;                // 결제 건 ID (txId)
    private String paymentId;         // 주문번호 (ex: order_12345678)
    private String status;            // paid, failed 등
    private String method;            // 결제 수단 (CARD 등)
    private int amount;               // 결제 금액
    private String currency;          // KRW 등
    private String pgProvider;        // kakaopay, tosspay 등
    private String orderName;         // 상품명 또는 주문명
    private String approvedAt;        // 승인 시각
}
