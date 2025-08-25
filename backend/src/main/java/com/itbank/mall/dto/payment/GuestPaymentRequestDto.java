package com.itbank.mall.dto.payment;

import lombok.Data;

@Data
public class GuestPaymentRequestDto {

    // 기본 결제 정보
    private String orderUid;
    private String productName;
    private int orderPrice;
    private int paidAmount;
    private String paymentMethod;
    private String status;
    private String paymentId;
    private String transactionId;
    private String pgProvider;

    private Long productId; // 상품 번호
    private Long productOptionId;
    private int quantity; // 주문 수량

    // 주문자 정보 (payment 테이블에 저장)
    private String buyerName; // 구매자 이름
    private String buyerEmail; // 구매자 이메일
    private String buyerPhone; // 구매자 휴대폰 번호

    // 수령자 정보 (deliveries 테이블에 저장)
    private String recipientName; // 수령인 이름
    private String recipientPhone; // 수령인 휴대폰 번호
    private String recipientAddress1; // 주소
    private String recipientAddress2; // 상세 주소
}
