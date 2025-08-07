package com.itbank.mall.dto.orders;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 주문 상세 정보를 담는 DTO.
 * 주문, 주문상품, 배송, 상품 데이터를 통합해서 표현한다.
 */
@Getter
@Setter
public class OrderDetailDto {

    // 주문 정보 -------------------------

    /** 주문 고유 번호 (PK) */
    private Long orderId;

    /** 주문 생성 시각 (yyyy-MM-dd HH:mm:ss) */
    private LocalDateTime createdAt;

    /** 주문 상태 (주문접수, 배송중, 완료 등) */
    private String orderStatus;

    // 상품 정보 -------------------------

    /** 상품 이름 */
    private String productName;

    /** 상품 개당 가격 */
    private int productPrice;

    /** 주문 수량 */
    private int quantity;

    /** 수량 * 개당가격 (개별 아이템 총액) */
    private int itemTotal;

    /** 상품 썸네일 이미지 URL */
    private String imageUrl;

    // 배송 정보 -------------------------
    private String buyerName;

    /** 배송지 주소 */
    private String deliveryAddress;

    /** 수령인 이름 */
    private String recipient;

    /** 수령인 전화번호 */
    private String phone;

    /** 운송장 번호 */
    private String trackingNumber;

    /** 배송 상태 (배송 준비 중, 배송 중, 배송 완료 등) */
    private String deliveryStatus;
}
