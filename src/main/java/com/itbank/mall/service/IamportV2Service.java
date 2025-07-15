package com.itbank.mall.service;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.PaymentV2ResponseDto;
import com.itbank.mall.util.DtoConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IamportV2Service {

    private final PaymentService paymentService;

    // ✅ 클라이언트에서 받은 결제 결과를 저장하는 메서드
    public String saveV2Payment(PaymentV2ResponseDto dto, Long memberId) {
        log.info("💾 PortOne 결제 결과 저장: {}", dto);

        // 🔄 클라이언트에서 받은 응답을 기존 방식 DTO로 변환
        var convertedDto = DtoConverter.toPaymentRequestDto(dto);

        // 🧾 DB 저장
        paymentService.savePaymentLog(convertedDto, dto.getId(), memberId);

        return dto.getId();  // tx_id로 사용
    }
}
