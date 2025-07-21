package com.itbank.mall.dto;

import lombok.Data;

@Data
public class PaymentV2ResponseDto {
	private String id; // 아임포트 결제 고유 ID (paymentId)
	private String orderName;
	private String amount;
	private String status;
	private String requestedAt;
	private String approvedAt;
	private String paymentMethod;
	private String cardNumber; // 마스킹된 카드번호
	private String customerIdentityNumber;
	private String pgProvider;
	private String customerEmail;
	private String customerName;
	private String customerAddress;
	private Long productId;  // 주문한 상품 ID
}
