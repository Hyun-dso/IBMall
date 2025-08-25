package com.itbank.mall.dto.track;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/** 회원 주문 상세 응답 DTO */
@Data
public class MemberOrderDetailResponse {
    private String orderUid;
    private LocalDateTime createdAt;
    private String status;
    private int totalPrice;

    private List<MemberOrderItemDto> items;
    private Delivery delivery;

    @Data
    public static class Delivery {
        private String recipient;
        private String phone;
        private String address1;
        private String address2;
        private String trackingNumber;
        private String status;
    }
}
