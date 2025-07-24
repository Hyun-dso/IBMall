package com.itbank.mall.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {

    @JsonProperty("imp_uid")
    private String impUid; // (선택) 저장할지 말지 결정

    @JsonProperty("merchant_uid")
    private String merchantUid;

    @JsonProperty("paid_amount")
    private int amount;

    private String name;

    @JsonProperty("buyer_email")
    private String buyerEmail;

    @JsonProperty("buyer_name")
    private String buyerName;

    @JsonProperty("buyer_tel")
    private String buyerPhone;

    private String pgProvider;

    private String buyerAddress;

    private Long productId;
}
