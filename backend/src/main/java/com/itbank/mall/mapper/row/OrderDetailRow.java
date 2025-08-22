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
}
