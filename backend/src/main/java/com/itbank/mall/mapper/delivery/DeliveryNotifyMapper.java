package com.itbank.mall.mapper.delivery;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.event.delivery.DeliveryNotifyInfo;

@Mapper
public interface DeliveryNotifyMapper {
    DeliveryNotifyInfo selectNotifyInfoByOrderId(@Param("orderId") Long orderId);
}