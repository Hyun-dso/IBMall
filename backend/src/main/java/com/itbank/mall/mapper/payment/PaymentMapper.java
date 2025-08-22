package com.itbank.mall.mapper.payment;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.entity.payment.Payment;

@Mapper
public interface PaymentMapper {

    // INSERT
    int insert(Payment payment);

    // 조회: 로컬 PK(id) 기반
    Integer findPaidAmountById(@Param("paymentId") Long paymentId);
    Long findMemberIdByPaymentId(@Param("paymentId") Long paymentId);

    // 조회: PortOne payment_id 기반 (추가)
    Integer findPaidAmountByPaymentId(@Param("paymentId") String paymentId);
    boolean existsByPaymentId(@Param("paymentId") String paymentId);
    Payment findByPaymentId(@Param("paymentId") String paymentId);

    // 조회: txId / orderUid
    boolean existsByTransactionId(@Param("transactionId") String transactionId);
    boolean existsByOrderUid(@Param("orderUid") String orderUid);
    Payment findByTransactionId(@Param("transactionId") String transactionId);
    Payment findByOrderUid(@Param("orderUid") String orderUid);

    // 주문 링크 업데이트
    int updateOrderId(Payment payment); // WHERE id = #{id}
    int updateOrderIdByTransactionId(@Param("transactionId") String transactionId,
                                     @Param("orderId") Long orderId);
    int updateOrderIdByPaymentId(@Param("paymentId") String paymentId,
                                 @Param("orderId") Long orderId);
}
