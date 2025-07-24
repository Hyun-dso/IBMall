package com.itbank.mall.dto.payment;

import lombok.Data;

@Data
public class V2PaymentLookupResponseDto {

    private String id;           // txId (transaction id)
    private int amount;          // 결제 금액
    private String method;       // 결제 수단 (ex. CARD)
    private String status;       // 결제 상태 (ex. paid)
    private String pgProvider;   // PG사 이름 (ex. kakaopay)
}
