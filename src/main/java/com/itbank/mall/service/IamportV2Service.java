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

        // ✅ 비회원이라면 주소 포함 모든 buyer 정보가 반드시 클라이언트에서 넘어와야 함
        // 이 로직은 사실상 생략 가능하나, 명시적으로 보강할 수도 있음
        if (memberId == null) {
        	convertedDto.setBuyerName(dto.getCustomerName());
        	convertedDto.setBuyerEmail(dto.getCustomerEmail());
        	convertedDto.setBuyerPhone(dto.getCustomerIdentityNumber());
        	convertedDto.setBuyerAddress(dto.getCustomerAddress()); // 프론트 받으면 여기에 세팅
        }

        // 🧾 결제 내역 + 주문 정보 저장
        paymentService.savePaymentLog(convertedDto, dto.getId(), memberId);

        return dto.getId();  // transaction ID
    }
}
