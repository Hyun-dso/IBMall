package com.itbank.mall.dto.admin.delivery;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryStatusDto {
    private String status;  // "배송중" 또는 "배송완료"
}
