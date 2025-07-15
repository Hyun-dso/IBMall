package com.itbank.mall.mapper;

import com.itbank.mall.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    int insert(Payment payment);
}
