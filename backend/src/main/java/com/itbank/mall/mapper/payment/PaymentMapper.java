package com.itbank.mall.mapper.payment;

import com.itbank.mall.entity.payment.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {

    int insert(Payment payment);
    int updateOrderId(Payment payment);

    long findMemberIdByPaymentId(@Param("paymentId") Long paymentId);
    int findPaidAmountById(@Param("paymentId") Long paymentId);

    // 🔹 멱등 체크/복구용 추가
    boolean existsByTransactionId(@Param("transactionId") String transactionId);
    boolean existsByOrderUid(@Param("orderUid") String orderUid);

    Payment findByTransactionId(@Param("transactionId") String transactionId);
    Payment findByOrderUid(@Param("orderUid") String orderUid);

    int updateOrderIdByTransactionId(@Param("transactionId") String transactionId,
                                     @Param("orderId") Long orderId);
}
