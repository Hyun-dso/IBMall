package com.itbank.mall.entity.settlement;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettlementDetailEntity {

    private Long id;
    private Long orderId;
    private Long productId;
    private Long memberId;
    private Long categoryId;

    private int quantity;
    private int amount;

    private int couponDiscount;
    private int mileageUsed;
    private int deliveryFee;
    private int deliveryCost;

    private int refundAmount;
    private String refundReason;

    private String type;
    private String status;
    private String paymentMethod;

    private LocalDate settlementDate;
    private String createdBy;
}
