// DeliveryStatusChangedEvent.java
package com.itbank.mall.event.delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryStatusChangedEvent {
    private final Long orderId;
    private final String newStatus;       // "배송중" | "배송완료"
    private final String trackingNumber;  // 배송중 전환 시 생성된 값
}
