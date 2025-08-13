package com.itbank.mall.mapper.orders;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.entity.orders.DeliveryEntity;

@Mapper
public interface DeliveryMapper {

    // UPSERT (INSERT ... ON DUPLICATE KEY UPDATE)
    int insertDelivery(DeliveryEntity delivery);

    // 관리자: 송장번호 세팅/교체
    int updateTrackingNumber(@Param("orderId") Long orderId,
                             @Param("trackingNumber") String trackingNumber);

    // 상태만 변경
    int updateStatus(@Param("orderId") Long orderId,
                     @Param("status") String status);

    // 조회: order_id로 단건
    DeliveryEntity findByOrderId(@Param("orderId") Long orderId);

    // 조회: tracking_number로 단건
    DeliveryEntity findByTrackingNumber(@Param("trackingNumber") String trackingNumber);
}
