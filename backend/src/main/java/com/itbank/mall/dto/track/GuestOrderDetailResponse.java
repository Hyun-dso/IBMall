package com.itbank.mall.dto.track;

import java.time.LocalDateTime;
import java.util.List;

import com.itbank.mall.dto.orders.GuestOrderItemDto;
import lombok.Data;

/** 비회원 주문 상세 + 배송 요약 */
@Data
public class GuestOrderDetailResponse {
    private String orderUid;
    private LocalDateTime createdAt;
    private String status;
    private int totalPrice;

    private List<GuestOrderItemDto> items;   // 기존 DTO 재사용

    private DeliveryBrief delivery;          // 배송 요약(마스킹 포함)

    @Data
    public static class DeliveryBrief {
        private String recipientMasked;      // 예: 김*령
        private String phoneMasked;          // 예: 010****8888
        private String address1Masked;      // 예: 서울 송파구 올림픽로 ***
        private String address2Masked;      // 예: ***-*** (또는 일부만 노출)
        private String trackingNumber;       // 있을 때만 세팅
        private String status;               // READY/SHIPPING/DELIVERED...
    }
}
