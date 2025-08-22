package com.itbank.mall.dto.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메일 본문 렌더링용 라인아이템 DTO
 * - DB에서는 lineTotal(= oi.price, 라인합계)만 내려오므로
 *   단가(unitPrice)는 서비스/리스너에서 lineTotal / quantity 로 계산한다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEmailLineDto {
    private String productName;  // p.name
    private String optionValue;  // po.option_value (nullable)
    private int quantity;        // oi.quantity
    private int lineTotal;       // oi.price (라인합계)
}
