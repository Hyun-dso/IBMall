package com.itbank.mall.event.payment;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentCompletedEvent {
    private final Long memberId;         // 게스트면 null
    private final String email;          // 멤버면 null 가능(멤버 DB에서 조회)
    private final String orderUid;
    private final List<String> productLines;
    private final int paidAmount;
}
