package com.itbank.mall.mapper.orders;

import org.apache.ibatis.annotations.Mapper;

import com.itbank.mall.entity.orders.DeliveryEntity;

@Mapper
public interface DeliveryMapper {
    int insertDelivery(DeliveryEntity delivery);
}
