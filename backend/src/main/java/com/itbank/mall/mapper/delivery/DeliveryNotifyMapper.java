package com.itbank.mall.mapper.delivery;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.event.delivery.DeliveryNotifySummaryDto;
import com.itbank.mall.event.delivery.DeliveryNotifyLineDto;

@Mapper
public interface DeliveryNotifyMapper {
    // 수신자/요약(닉네임/이메일/UID/운송장/총액/개수)
    DeliveryNotifySummaryDto findDeliveryNotifySummaryByOrderId(@Param("orderId") Long orderId);

    // 상품 라인(상품명/옵션/수량/단가/합계/이미지)
    List<DeliveryNotifyLineDto> findDeliveryNotifyLinesByOrderId(@Param("orderId") Long orderId);
}
