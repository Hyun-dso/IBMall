// src/main/java/com/itbank/mall/service/payment/PaymentVerificationService.java
package com.itbank.mall.service.payment;

import com.itbank.mall.integrations.portone.PortoneV2Client;
import com.itbank.mall.integrations.portone.dto.VerifiedPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentVerificationService {

    private final PortoneV2Client client;

    /** txId로 서버 검증 후, 금액 일치/상태=PAID가 아니면 예외 */
    public VerifiedPayment verifyOrThrow(String txId, int expectedAmount) {
        VerifiedPayment v = client.getPaymentByTxId(txId);
        if (v.getAmount() == null || v.getAmount() != expectedAmount) {
            throw new IllegalArgumentException("결제 금액 불일치");
        }
        if (!"PAID".equals(v.getStatus())) {
            throw new IllegalStateException("결제 미승인 상태: " + v.getStatus());
        }
        return v;
    }
}
