package com.itbank.mall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private String merchant_uid;  // 주문 고유 번호 (ex: uuid)
    private String name;          // 상품명
    private int amount;           // 결제금액 (1원)
    private String buyer_email;
    private String buyer_name;
    private String buyer_tel;
}
