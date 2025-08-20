package com.itbank.mall.dto.settlement;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExpectedSettlementDto {

    private Long id;                       // 정산 고유 ID
    private Long orderId;                  // 주문 ID
    private Long productId;                // 상품 ID

    private String productName;            // 상품명 (정산 시점 기준)
    private int quantity;                  // 수량
    private int finalPrice;                // 최종 결제 금액

    private LocalDateTime paymentCreatedAt;   // 결제일자
    private LocalDateTime createdAt;           // 정산 데이터 저장일자
}
