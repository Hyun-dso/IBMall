package com.itbank.mall.service.settlement;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itbank.mall.dto.settlement.SettlementDetailDto;
import com.itbank.mall.entity.settlement.SettlementDetailEntity;
import com.itbank.mall.mapper.settlement.SettlementDetailMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementDetailService {

    private final SettlementDetailMapper settlementDetailMapper;

    // ✅ 정산 저장
    public int saveSettlement(SettlementDetailEntity entity) {
        // 실정산(type=actual)만 허용
        if (!"actual".equals(entity.getType())) {
            return 0;
        }

        // 주문의 배송 상태 조회
        String status = settlementDetailMapper.selectDeliveryStatusByOrderId(entity.getOrderId());

        // 배송완료가 아닐 경우 저장 불가
        if (!"배송완료".equals(status)) {
            return 0;
        }

        return settlementDetailMapper.insertSettlement(entity);
    }


    // ✅ 전체 정산 조회
    public List<SettlementDetailDto> getAllSettlements() {
        return settlementDetailMapper.selectAllSettlements();
    }

    // ✅ 정산 타입별 조회
    public List<SettlementDetailDto> getSettlementsByType(String type) {
        return settlementDetailMapper.selectByType(type);
    }

    // ✅ 날짜 필터 정산 조회
    public List<SettlementDetailDto> getSettlementsByDateRange(String start, String end) {
        return settlementDetailMapper.selectByDateRange(start, end);
    }

    // ✅ 단건 조회 (주문번호)
    public SettlementDetailDto getSettlementByOrderId(Long orderId) {
        return settlementDetailMapper.selectByOrderId(orderId);
    }
    public List<SettlementDetailDto> search(String type, String start, String end,
            Long memberId, Long productId, Long categoryId, String status) {
return settlementDetailMapper.search(type, start, end, memberId, productId, categoryId, status);
}

}
