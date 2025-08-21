package com.itbank.mall.mapper.payment;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.entity.payment.Payment;

@Mapper
public interface PaymentMapper {

    int insert(Payment payment);

    Integer findPaidAmountById(@Param("paymentId") Long paymentId);

    Long findMemberIdByPaymentId(@Param("paymentId") Long paymentId);

    int updateOrderId(Payment payment);

    boolean existsByTransactionId(@Param("transactionId") String transactionId);

    boolean existsByOrderUid(@Param("orderUid") String orderUid);

    Payment findByTransactionId(@Param("transactionId") String transactionId);

    int updateOrderIdByTransactionId(@Param("transactionId") String transactionId,
                                     @Param("orderId") Long orderId);

    Payment findByOrderUid(@Param("orderUid") String orderUid);
}
