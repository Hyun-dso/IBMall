// src/main/java/com/itbank/mall/integrations/portone/dto/VerifiedPayment.java
package com.itbank.mall.integrations.portone.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VerifiedPayment {
    String txId;           // 조회 기준
    String status;         // PAID/CANCELLED/FAILED 등
    Integer amount;        // 총 결제 금액
    String currency;       // KRW
    String payMethod;      // CARD/KAKAOPAY/...
    String easyPayProvider;// KAKAOPAY/PAYCO/... (없을 수 있음)
}
