// com/itbank/mall/mapper/row/OrderSummaryRow.java
package com.itbank.mall.mapper.row;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrderSummaryRow {
    private Long orderId;
    private String orderUid;
    private LocalDateTime createdAt;
    private String status;
    private int totalPrice;

    private String buyerName;
    private String buyerPhone;

    private String itemSummary;   // "맥북 외 1개"
    private boolean hasDelivery;  // 배송 존재 여부
}
