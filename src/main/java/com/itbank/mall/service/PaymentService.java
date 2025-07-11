package com.itbank.mall.service;

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
        payment.setOrderId(dto.getMerchant_uid());
        payment.setMemberId(memberId);
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod("card"); // 예시
        payment.setStatus("paid");
        payment.setTransactionId(transactionId);

        paymentMapper.insert(payment);
    }
}
