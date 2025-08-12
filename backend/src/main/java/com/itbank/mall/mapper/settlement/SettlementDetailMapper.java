package com.itbank.mall.mapper.settlement;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.itbank.mall.dto.settlement.SettlementDetailDto;
import com.itbank.mall.entity.settlement.SettlementDetailEntity;

public interface SettlementDetailMapper {

    // INSERT: 실정산 저장
    int insertSettlement(SettlementDetailEntity entity);

    // 전체 조회 (JOIN 포함)
    List<SettlementDetailDto> selectAllSettlements();

    // 타입별 조회 (actual / expected / refund 등)
    List<SettlementDetailDto> selectByType(@Param("type") String type);

    // 날짜 범위 조회 (정산일 기준)
    List<SettlementDetailDto> selectByDateRange(@Param("startDate") String startDate,
                                                @Param("endDate") String endDate);

    // 단건 조회 (주문번호 기준)
    SettlementDetailDto selectByOrderId(Long orderId);

    // 배송 상태 조회 (deliveries.status)
    String selectDeliveryStatusByOrderId(Long orderId);

    // 통합 검색
    List<SettlementDetailDto> search(@Param("type") String type,
                                     @Param("start") String start,
                                     @Param("end") String end,
                                     @Param("memberId") Long memberId,
                                     @Param("productId") Long productId,
                                     @Param("categoryId") Long categoryId,
                                     @Param("status") String status);
}
