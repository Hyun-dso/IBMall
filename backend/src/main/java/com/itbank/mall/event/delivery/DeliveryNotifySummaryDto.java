// src/main/java/com/itbank/mall/event/delivery/DeliveryMailSummaryDto.java
package com.itbank.mall.event.delivery;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryNotifySummaryDto {
	private Long orderId;
	private String orderUid;
	private String transactionId; // payment.transaction_id (없어도 됨)
	private String trackingNumber; // deliveries.tracking_number (없어도 됨)
	private String deliveryStatus; // deliveries.status (없어도 됨)
	private Long totalAmount; // SUM(oi.price*oi.quantity) or 0
	private Integer itemCount; // COUNT(*)
	private String memberEmail; // 수신자
	private String memberNickname; // 인사말
}
