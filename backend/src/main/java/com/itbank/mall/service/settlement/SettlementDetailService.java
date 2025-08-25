package com.itbank.mall.service.settlement;

import static com.itbank.mall.service.settlement.SaveResult.Status.AMOUNT_MISMATCH;
import static com.itbank.mall.service.settlement.SaveResult.Status.DELIVERY_NOT_DELIVERED;
import static com.itbank.mall.service.settlement.SaveResult.Status.INSERT_FAILED;
import static com.itbank.mall.service.settlement.SaveResult.Status.INVALID_TYPE;
import static com.itbank.mall.service.settlement.SaveResult.Status.PAYMENT_NOT_PAID;
import static com.itbank.mall.service.settlement.SaveResult.Status.SUCCESS;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.domain.enums.DeliveryStatus;
import com.itbank.mall.dto.settlement.SettlementDetailDto;
import com.itbank.mall.entity.settlement.SettlementDetailEntity;
import com.itbank.mall.mapper.settlement.SettlementDetailMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementDetailService {

    private final SettlementDetailMapper settlementDetailMapper;

    // ✅ 정산 저장 (게이트 + 금액 검증 + 사유 전달)
    @Transactional(rollbackFor = Exception.class) 
    public SaveResult saveSettlement(SettlementDetailEntity entity) {
        // 1) 실정산만 허용
        if (!"actual".equals(entity.getType())) {
            return new SaveResult(INVALID_TYPE, null, "실정산(type=actual)만 저장 가능합니다");
        }

        // 2) 결제 상태: PAID 존재 여부
        int paidExists = settlementDetailMapper.existsPaidPaymentByOrderId(entity.getOrderId());
        if (paidExists <= 0) {
            return new SaveResult(PAYMENT_NOT_PAID, null, "결제 상태가 PAID가 아닙니다");
        }

        // 3) 배송 상태: DELIVERED
        String delivery = settlementDetailMapper.selectDeliveryStatusByOrderId(entity.getOrderId());
        if (delivery == null || !DeliveryStatus.DELIVERED.name().equals(delivery)) {
            return new SaveResult(DELIVERY_NOT_DELIVERED, null, "배송 상태가 DELIVERED가 아닙니다");
        }

        // 4) 금액 정합성: Σ(oi.price*qty) == payment.paid_amount == orders.total_price
        int serverSum = settlementDetailMapper.sumOrderItemsAmount(entity.getOrderId());
        Integer paidAmount = settlementDetailMapper.selectPaymentPaidAmount(entity.getOrderId());
        Integer ordersTotal = settlementDetailMapper.selectOrdersTotalPrice(entity.getOrderId());
        if (paidAmount == null || ordersTotal == null || serverSum != paidAmount || !paidAmount.equals(ordersTotal)) {
            return new SaveResult(AMOUNT_MISMATCH, null, "금액 불일치(서버합계, payment.paid_amount, orders.total_price)");
        }

        // 5) 통과 시 INSERT
        int inserted = settlementDetailMapper.insertSettlement(entity);
        if (inserted > 0) {
            // PK 반환이 필요하면 mapper에 selectKey를 붙여서 entity.getId()로 세팅 가능
            return new SaveResult(SUCCESS, null, "정산 저장 완료");
        }
        return new SaveResult(INSERT_FAILED, null, "정산 저장 실패");
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
