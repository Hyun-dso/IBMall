package com.itbank.mall.service.payment;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.payment.PaymentRequestDto;
import com.itbank.mall.dto.payment.PaymentV2ResponseDto;
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

        if (dto.getCustomerAddress() == null || dto.getCustomerAddress().isBlank()) {
            throw new IllegalArgumentException("❌ 고객 주소 누락됨");
        }

        PaymentRequestDto convertedDto = DtoConverter.toPaymentRequestDto(dto);

        if (memberId == null) {
            convertedDto.setBuyerName(dto.getCustomerName());
            convertedDto.setBuyerEmail(dto.getCustomerEmail());
            convertedDto.setBuyerPhone(dto.getCustomerIdentityNumber());
            convertedDto.setBuyerAddress(dto.getCustomerAddress());
        }

        log.info("🧾 변환된 DTO: {}", convertedDto);

        paymentService.savePaymentLog(convertedDto, dto.getId(), memberId);

        return dto.getId();
    }
}
