package com.itbank.mall.service;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.PaymentV2ResponseDto;
import com.itbank.mall.dto.PaymentRequestDto;
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
        PaymentRequestDto convertedDto = DtoConverter.toPaymentRequestDto(dto);

        // ✅ 비회원이라면 이름, 이메일, 전화번호 주소 반영
        if (memberId == null) {
            convertedDto.setBuyer_name(dto.getCustomerName());
            convertedDto.setBuyer_email(dto.getCustomerEmail());
            convertedDto.setBuyer_tel(dto.getCustomerIdentityNumber());
            convertedDto.setBuyer_Address("비회원 주소 없음");  // ⚠️ 이후 프론트에서 받으면 여기에 세팅
        }

        // 🧾 결제 내역 + 주문 정보 저장
        paymentService.savePaymentLog(convertedDto, dto.getId(), memberId);

        return dto.getId();  // transaction ID
    }
}
