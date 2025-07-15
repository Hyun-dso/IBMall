package com.itbank.mall.util;

import com.itbank.mall.dto.PaymentRequestDto;
import com.itbank.mall.dto.PaymentV2ResponseDto;

public class DtoConverter {

    public static PaymentRequestDto toPaymentRequestDto(PaymentV2ResponseDto v2) {
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setAmount(Integer.parseInt(v2.getAmount()));
        dto.setName(v2.getOrderName());
        dto.setPgProvider(v2.getPgProvider());
        dto.setMerchant_uid("order_" + System.currentTimeMillis());
        dto.setBuyer_email("v2test@example.com"); // 테스트용 임시 값
        dto.setBuyer_name("V2 유저");
        dto.setBuyer_tel("010-0000-0000");
        return dto;
    }
}
