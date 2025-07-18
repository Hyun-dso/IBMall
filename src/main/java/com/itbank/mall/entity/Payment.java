package com.itbank.mall.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Payment {
    private Long id;
    private Long memberId;
    private String orderUid;        // 🔁 orderId → orderUid
    private String productName;     // 🔁 추가
    private int orderPrice;         // 🔁 amount → orderPrice
    private int paidAmount;         // 🔁 추가
    private String paymentMethod;
    private String status;
    private String transactionId;
    private String pgProvider;
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;
    private String buyerAddress;
    private LocalDateTime createdAt;
}
