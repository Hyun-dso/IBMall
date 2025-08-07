package com.itbank.mall.mapper.payment;

import com.itbank.mall.entity.payment.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {

    int insert(Payment payment);
    
    // ✅ 추가: 결제 ID로 회원 ID 조회
    long findMemberIdByPaymentId(@Param("paymentId") Long paymentId);

    // 🔹 결제 ID로 결제 금액 조회
    int findPaidAmountById(@Param("paymentId") Long paymentId);
}
