package com.itbank.mall.mapper.orders;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itbank.mall.dto.orders.OrderDto;
import com.itbank.mall.entity.orders.OrderEntity;
import com.itbank.mall.entity.orders.OrderItemEntity;

// 신규 Row(VO)
import com.itbank.mall.mapper.row.OrderSummaryRow;
import com.itbank.mall.mapper.row.OrderDetailRow;
import com.itbank.mall.mapper.row.OrderItemRow;

@Mapper
public interface OrderMapper {

    // ===== 기존 기능 =====
    // 특정 회원의 전체 주문 내역 조회
    List<OrderDto> findOrdersByMemberId(@Param("memberId") Long memberId);
    int insertOrder(OrderEntity order);
    int insertOrderItem(OrderItemEntity item);

    // ===== 비회원 주문 조회(이름+전화) 추가 =====
    long countGuestOrdersByNamePhone(
        @Param("name") String name,
        @Param("phone") String phone,
        @Param("from") LocalDate from,
        @Param("to") LocalDate to
    );

    List<OrderSummaryRow> selectGuestOrdersByNamePhone(
        @Param("name") String name,
        @Param("phone") String phone,
        @Param("from") LocalDate from,
        @Param("to") LocalDate to,
        @Param("offset") int offset,
        @Param("size") int size
    );

    OrderDetailRow selectGuestOrderDetail(
        @Param("orderUid") String orderUid,
        @Param("name") String name,
        @Param("phone") String phone
    );

    // 주문 아이템 조회 (상세용)
    List<OrderItemRow> selectOrderItems(@Param("orderId") Long orderId);
    
    // ===== Member 전용 조회(신규) =====
    long countOrdersByMemberId(@Param("memberId") Long memberId,
                               @Param("from") LocalDate from,
                               @Param("to") LocalDate to,
                               @Param("status") String statusOrNull);

    List<OrderSummaryRow> selectOrdersByMemberId(@Param("memberId") Long memberId,
                                                 @Param("from") LocalDate from,
                                                 @Param("to") LocalDate to,
                                                 @Param("status") String statusOrNull,
                                                 @Param("offset") int offset,
                                                 @Param("size") int size);

    OrderDetailRow selectMemberOrderDetail(@Param("memberId") Long memberId,
                                           @Param("orderUid") String orderUid);
}
