package com.itbank.mall.entity.orders;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryEntity {
    private Long deliveryId;       // 배송 ID (PK)
    private Long orderId;          // 주문 ID (FK)
    private String address1;        // 배송 주소
    private String address2;        // 배송 상세 주소
    private String recipient;      // 수령인 이름
    private String phone;          // 수령인 연락처
    private String trackingNumber; // 운송장 번호
    private String status;         // 배송 상태 (예: 배송 준비, 배송 중, 배송 완료 등)
}
