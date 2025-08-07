package com.itbank.mall.mapper.orders;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderItemMapper {

    // 리뷰 여부 확인
    boolean isReviewed(@Param("orderItemId") Long orderItemId);

    // 리뷰 완료 처리
    int markAsReviewed(@Param("orderItemId") Long orderItemId);
}
