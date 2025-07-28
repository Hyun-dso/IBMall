package com.itbank.mall.service.orders;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.entity.orders.DeliveryEntity;
import com.itbank.mall.mapper.orders.DeliveryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryMapper deliveryMapper;

    @Transactional
    public void saveDelivery(DeliveryEntity delivery) {
        deliveryMapper.insertDelivery(delivery);
    }
}
