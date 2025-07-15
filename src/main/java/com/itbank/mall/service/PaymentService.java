package com.itbank.mall.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.PaymentRequestDto;
import com.itbank.mall.entity.Payment;
import com.itbank.mall.mapper.PaymentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;
    
    public void savePaymentLog(PaymentRequestDto dto, String transactionId, Long memberId) {
        Payment payment = new Payment();
        payment.setMemberId(memberId);
        payment.setOrderUid(dto.getMerchant_uid());     // ✅ order_uid로 일치
        payment.setProductName(dto.getName());          // ✅ product_name 추가
        payment.setOrderPrice(dto.getAmount());         // ✅ order_price로 변경
        payment.setPaidAmount(dto.getAmount());         // ✅ paid_amount 추가
        payment.setPaymentMethod("card");               // TODO: 실제 결제 수단 사용 가능
        payment.setStatus("paid");                      // TODO: 실제 결제 상태 활용 가능
        payment.setTransactionId(transactionId);
        payment.setCreatedAt(LocalDateTime.now());      // ✅ created_at 명시적으로 저장

        paymentMapper.insert(payment);  // 매퍼 XML과 1:1 매칭됨
    }
}
