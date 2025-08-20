package com.itbank.mall.dto.payment;

import java.util.List;
import com.itbank.mall.dto.orders.MemberOrderItemDto;
import lombok.Data;

@Data
public class MemberCartPaymentRequestDto {

    // 결제 정보
    private String orderUid;
    private String paymentMethod;
    private int paidAmount;
    private int originalAmount;
    private String status;
    private String paymentId;
    private String transactionId;
    private String pgProvider;

    // 할인 정보
    private Long couponId;         // 사용한 쿠폰 ID
    private int usedMileage;
    private int gradeDiscount;

    // 배송 정보
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;

    // 장바구니 상품 정보
    private List<MemberOrderItemDto> items;
}
