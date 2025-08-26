package com.itbank.mall.dto.payment;

import java.util.List;

import com.itbank.mall.dto.orders.GuestOrderItemDto;

import lombok.Data;

@Data
public class GuestCartPaymentRequestDto {

    // 기본 결제 정보
    private String orderUid;
    private String productName;      // ex) "노트북 외 2개"
    private int orderPrice;          // 총 주문 금액
    private int paidAmount;
    private String paymentMethod;
    private String status;
    private String paymentId;
    private String transactionId;
    private String pgProvider;

    // 주문자 정보
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;

    // 수령자 정보
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress1;
    private String recipientAddress2;

    // 🧩 장바구니 내 상품 리스트
    private List<GuestOrderItemDto> items;
}
