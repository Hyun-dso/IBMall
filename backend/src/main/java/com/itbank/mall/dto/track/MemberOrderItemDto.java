package com.itbank.mall.dto.track;

import lombok.Data;

/** 회원 주문 상세 내 아이템 DTO */
@Data
public class MemberOrderItemDto {
    private Long productId;
    private Long productOptionId;
    private String productName;
    private String optionName; // 옵션 없으면 null
    private int quantity;
    private int unitPrice;
}
