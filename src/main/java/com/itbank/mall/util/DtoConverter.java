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

        // 기본값 설정 (비회원용)
        dto.setBuyer_address(v2.getCustomerAddress());  // ✅ 통합 주소 필드 사용
        dto.setBuyer_tel(v2.getCustomerIdentityNumber());  // ✅ tel도 그대로
        dto.setBuyer_name(v2.getCustomerName());
        dto.setBuyer_email(v2.getCustomerEmail());

        dto.setProductId(v2.getProductId());         // ✅ 상품 ID

        return dto;
    }
}

