package com.itbank.mall.service.payment;

import com.itbank.mall.integrations.portone.PortoneV2Client;
import com.itbank.mall.integrations.portone.dto.VerifiedPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentVerificationService {

    private final PortoneV2Client client;

    /**
     * V2 기준: paymentId로 우선 검증.
     * (임시 호환) paymentId가 비어있으면 txId로 폴백.
     */
    public VerifiedPayment verifyOrThrow(String paymentId, String txId, int expectedAmount) {
        VerifiedPayment v;

        if (paymentId != null && !paymentId.isBlank()) {
            v = client.getPaymentByPaymentId(paymentId);   // ✅ 새 경로
        } else {
            // (임시) 프론트가 아직 paymentId 안 줄 때를 위한 폴백
            v = client.getPaymentByTxId(txId);
        }

        if (v.getAmount() == null || v.getAmount() != expectedAmount) {
            throw new IllegalArgumentException("결제 금액 불일치");
        }
        if (!"SUCCESS".equalsIgnoreCase(v.getStatus())) {
            throw new IllegalStateException("결제 미승인 상태: " + v.getStatus());
        }
        return v;
    }
}
