package com.itbank.mall.dto.orders.detail;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 회원/비회원 공용 주문 상세 응답 DTO
 * - 결제 세부정보(카드/트랜잭션ID 등) 노출 없음
 * - 헤더 텍스트: "첫상품(옵션) x수량 외 N개 · 총 {paidAmount}원"
 */
@Data
public class OrderDetailResponse {

    private Summary summary;               // 주문 요약
    private Buyer buyer;                   // 주문자 (비회원 경로는 마스킹 적용)
    private Recipient recipient;           // 수령자/배송 정보
    private List<OrderItemLine> items;     // 라인아이템 목록

    @Data
    public static class Summary {
        private String orderUid;           // 주문 UID (외부 조회키)
        private LocalDateTime createdAt;   // 주문일시
        private String status;             // ORDERED/SHIPPING/DELIVERED/...
        private int itemsCount;            // 라인 수
        private int paidAmount;            // 총 결제금액 (= orders.total_price)
        private String headerText;         // "첫상품(옵션) x수량 외 N개 · 총 0원"
    }

    @Data
    public static class Buyer {
        private String name;               // 비회원: 마스킹 적용
        private String phone;              // 비회원: 마스킹 적용(예: 010****5678)
    }

    @Data
    public static class Recipient {
        private String name;               // 비회원: 마스킹 적용
        private String phone;              // 비회원: 마스킹 적용
        private String address1;           // 비회원: 일부 마스킹
        private String address2;           // 비회원: 일부 마스킹
        private String trackingNumber;     // 있을 때만 세팅
        private String deliveryStatus;     // READY/SHIPPING/DELIVERED/...
    }

    @Data
    public static class OrderItemLine {
        private Long productId;
        private Long productOptionId;      // 옵션 없으면 null
        private String productName;
        private String optionName;         // product_option.option_value (없으면 null)
        private int quantity;
        private int unitPrice;             // 단가 = order_items.price
        private int lineTotal;             // unitPrice * quantity
    }
}
