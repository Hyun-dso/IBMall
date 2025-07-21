package com.itbank.mall.util;

import com.itbank.mall.dto.PaymentRequestDto;
import com.itbank.mall.dto.PaymentV2ResponseDto;

public class DtoConverter {

    public static PaymentRequestDto toPaymentRequestDto(PaymentV2ResponseDto v2) {
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setAmount(Integer.parseInt(v2.getAmount()));
        dto.setName(v2.getOrderName());
        dto.setPgProvider(v2.getPgProvider());
        dto.setMerchantUid("order_" + System.currentTimeMillis());

        // 기본값 설정 (비회원용)
        dto.setBuyerAddress(v2.getCustomerAddress());
        dto.setBuyerPhone(v2.getCustomerIdentityNumber());
        dto.setBuyerName(v2.getCustomerName());
        dto.setBuyerEmail(v2.getCustomerEmail());

        dto.setProductId(v2.getProductId());         // ✅ 상품 ID

        return dto;
    }
}

