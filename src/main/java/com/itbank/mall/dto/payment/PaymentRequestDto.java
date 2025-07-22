package com.itbank.mall.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private String merchantUid;     // 주문 고유 번호
    private String name;            // 상품명
    private int amount;             // 결제금액
    private String buyerEmail;      // 이메일
    private String buyerName;       // 이름
    private String buyerPhone;        // 전화번호
    private String buyerAddress;    // 주소
    private String pgProvider;      // ex: kakaopay, tosspay, html5_inicis
    private Long productId;         // 상품번호
}
