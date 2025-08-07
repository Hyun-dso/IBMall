package com.itbank.mall.dto.settlement;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettlementDetailDto {

    private Long id;
    private Long orderId;
    private Long productId;
    private Long memberId;
    private Long categoryId;

    private int quantity;
    private int amount;               // price → amount로 수정

    private int couponDiscount;
    private int mileageUsed;
    private int deliveryFee;
    private int deliveryCost;

    private int refundAmount;
    private String refundReason;

    private String type;             // actual / expected / refund
    private String status;           // complete / cancel / exchange 등
    private String paymentMethod;

    private LocalDate settlementDate;
    private String createdBy;
    private String productName;
}
