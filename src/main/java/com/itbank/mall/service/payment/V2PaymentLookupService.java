package com.itbank.mall.service.payment;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.payment.V2PaymentLookupResponseDto;
import com.itbank.mall.dto.payment.V2PaymentResultDto;
import com.itbank.mall.util.PortOneV2Client;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class V2PaymentLookupService {

    private final PortOneV2Client portOneV2Client;

    public V2PaymentLookupResponseDto getPaymentResultByTxId(String txId) {
        V2PaymentResultDto dto = portOneV2Client.getPaymentResultByTxId(txId);

        // 변환 로직
        V2PaymentLookupResponseDto response = new V2PaymentLookupResponseDto();
        response.setId(dto.getId());
        response.setAmount(dto.getAmount());
        response.setMethod(dto.getMethod());
        response.setStatus(dto.getStatus());
        response.setPgProvider(dto.getPgProvider());

        return response;
    }
}