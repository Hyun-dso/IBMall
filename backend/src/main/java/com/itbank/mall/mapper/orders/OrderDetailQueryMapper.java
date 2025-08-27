package com.itbank.mall.mapper.orders;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.mapper.row.OrderDetailRow;
import com.itbank.mall.mapper.row.OrderItemRow;

@Mapper
public interface OrderDetailQueryMapper {

    /** 주문 헤더 조회 (orders + deliveries + payment) */
    OrderDetailRow selectOrderHeaderByUid(@Param("orderUid") String orderUid);

    /** 주문 아이템 목록 조회 (order_items + product + product_option) */
    List<OrderItemRow> selectOrderItemsByOrderId(@Param("orderId") Long orderId);
}
