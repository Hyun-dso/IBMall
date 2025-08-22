// com/itbank/mall/mapper/row/OrderItemRow.java
package com.itbank.mall.mapper.row;

import lombok.Data;

@Data
public class OrderItemRow {
    private Long orderId;
    private Long productId;
    private Long productOptionId;

    private String productName; // product.name
    private String optionName;  // product_option.name (없으면 null)
    private int quantity;
    private int unitPrice;
}
