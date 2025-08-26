// DeliveryNotifyInfo.java
package com.itbank.mall.event.delivery;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeliveryNotifyInfo {
    private Long orderId;
    private String memberEmail;
    private String memberNickname;
    private String trackingNumber;
    private String status;
}
