// src/main/java/com/itbank/mall/integrations/portone/PortoneV2Client.java
package com.itbank.mall.integrations.portone;

import com.fasterxml.jackson.databind.JsonNode;
import com.itbank.mall.integrations.portone.dto.VerifiedPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortoneV2Client {

    private final WebClient portoneWebClient;

    public VerifiedPayment getPaymentByTxId(String txId) {
        // 엔드포인트: V2 결제 단건 조회(txId 기반)
        JsonNode root = portoneWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/payments/{txId}")
                        .build(txId))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (root == null) {
            throw new IllegalStateException("Empty response from PortOne");
        }

        // 안전 파싱
        String status   = text(root, "status");
        Integer amount  = intOrNull(root.at("/amount/total")); // amount.total
        String currency = text(root.at("/amount/currency"));
        String method   = text(root, "payMethod");             // payMethod 우선
        if (method == null || method.isBlank()) {
            method = text(root, "method");                    // fallback
        }
        String easy     = text(root, "easyPayProvider");

        return VerifiedPayment.builder()
                .txId(txId)
                .status(normalizeStatus(status))
                .amount(amount)
                .currency(currency)
                .payMethod(method != null ? method.toUpperCase() : null)
                .easyPayProvider(easy)
                .build();
    }

    private static String text(JsonNode n, String field) {
        return n != null && n.has(field) && !n.get(field).isNull() ? n.get(field).asText() : null;
    }
    private static String text(JsonNode n) {
        return (n != null && !n.isNull()) ? n.asText(null) : null;
    }
    private static Integer intOrNull(JsonNode n) {
        return (n != null && n.isInt()) ? n.asInt() : (n != null && n.isNumber()) ? n.numberValue().intValue() : null;
    }
    private static String normalizeStatus(String s) {
        if (s == null) return null;
        s = s.trim();
<<<<<<< HEAD
        return "paid".equalsIgnoreCase(s) ? "SUCCESS"
        		: "success".equalsIgnoreCase(s) ? "SUCCESS"
                : "cancelled".equalsIgnoreCase(s) ? "CANCELLED"
                : "refunded".equalsIgnoreCase(s) ? "REFUNDED"
                : "failed".equalsIgnoreCase(s) ? "FAILED"
                : s.toUpperCase();
=======
        return "paid".equalsIgnoreCase(s) ? "SUCCESS"   // PortOne 응답 paid → SUCCESS
             : "success".equalsIgnoreCase(s) ? "SUCCESS" // 혹시 success도 오면 SUCCESS
             : "cancelled".equalsIgnoreCase(s) ? "CANCELLED"
             : "refunded".equalsIgnoreCase(s) ? "REFUNDED"
             : "failed".equalsIgnoreCase(s) ? "FAILED"
             : s.toUpperCase();
>>>>>>> origin/0819
    }
}
