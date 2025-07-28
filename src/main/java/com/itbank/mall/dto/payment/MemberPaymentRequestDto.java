package com.itbank.mall.dto.payment;

import lombok.Data;

@Data
public class MemberPaymentRequestDto {

    // 결제 정보
    private String orderUid;
    private String paymentMethod;
    private int paidAmount;        // 실제 결제된 금액 (할인 적용 후)
    private int originalAmount;    // 총 주문 금액 (할인 전)
    private String status;
    private String transactionId;
    private String pgProvider;

    // 상품 정보 (단일 상품부터 시작)
    private Long productId;
    private Long productOptionId;
    private int quantity;

    // 할인 정보
    private Long couponId;         // 사용한 쿠폰 ID (nullable)
    private int usedMileage;       // 사용한 마일리지
    private int gradeDiscount;     // 멤버 등급 할인 금액

    // 배송 정보
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
}
