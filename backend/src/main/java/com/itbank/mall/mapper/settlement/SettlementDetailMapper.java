package com.itbank.mall.mapper.settlement;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.itbank.mall.dto.settlement.SettlementDetailDto;
import com.itbank.mall.entity.settlement.SettlementDetailEntity;

public interface SettlementDetailMapper {

    // 정산 저장 (Entity 기반)
    int insertSettlement(SettlementDetailEntity entity);

    // 정산 전체 조회 (Dto 기반, JOIN 포함)
    List<SettlementDetailDto> selectAllSettlements();

    // 타입별 정산 조회 (예: 실정산 only)
    List<SettlementDetailDto> selectByType(String settlementType);

    // 날짜 범위별 정산 조회
    List<SettlementDetailDto> selectByDateRange(String startDate, String endDate);

    // 단건 상세 조회 (주문번호 기준)
    SettlementDetailDto selectByOrderId(Long orderId);
    
    String selectDeliveryStatusByOrderId(Long orderId);
    
    List<SettlementDetailDto> search(
    		@Param("type") String type,
            @Param("start") String start,
            @Param("end") String end,
            @Param("memberId") Long memberId,
            @Param("productId") Long productId,
            @Param("categoryId") Long categoryId,
            @Param("status") String status);

}
