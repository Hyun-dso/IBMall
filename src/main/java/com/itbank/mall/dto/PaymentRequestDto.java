package com.itbank.mall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private String merchant_uid;   // 주문 고유 번호
    private String name;           // 상품명
    private int amount;            // 결제금액
    private String buyer_email;    // 이메일
    private String buyer_name;     // 이름
    private String buyer_tel;      // 전화번호
    private String buyer_address;  // 주소
    private String pgProvider;     // ex: kakaopay, tosspay, html5_inicis
    private Long productId;  // 상품번호
}
