package com.itbank.mall.mapper.payment;

import org.apache.ibatis.annotations.Mapper;

import com.itbank.mall.entity.payment.Payment;

@Mapper
public interface PaymentMapper {
    int insert(Payment payment);
}
