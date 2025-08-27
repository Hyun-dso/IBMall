// com/itbank/mall/mapper/row/OrderDetailRow.java
package com.itbank.mall.mapper.row;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrderDetailRow {
    private Long orderId;
    private String orderUid;
    private LocalDateTime createdAt;
    private String status;
    private int totalPrice;

    private String buyerName;
    private String buyerPhone;
    
    private String recipient;        // deliveries.recipient
    private String deliveryPhone;    // deliveries.phone
    private String address1;         // deliveries.address1
    private String address2;         // deliveries.address2
    private String trackingNumber;   // deliveries.tracking_number
    private String deliveryStatus;   // deliveries.status

    private Integer paidAmount;      // payment.paid_amount (검증용, null 가능)
}
