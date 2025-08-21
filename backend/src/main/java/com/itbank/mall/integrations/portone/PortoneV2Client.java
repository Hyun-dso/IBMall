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

    private final WebClient portoneWebClient; // baseUrl = https://api.portone.io  (properties)

    /**
     * V2 결제 단건 조회: paymentId 기반 (권장)
     * GET /payments/{paymentId}
     */
    public VerifiedPayment getPaymentByPaymentId(String paymentId) {
        JsonNode root = portoneWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/payments/{paymentId}")
                        .build(paymentId))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (root == null) {
            throw new IllegalStateException("Empty response from PortOne");
        }

        // 안전 파싱
        String status   = text(root, "status");
        Integer amount  = intOrNull(root.at("/amount/total"));   // amount.total
        String currency = text(root.at("/amount/currency"));
        String method   = firstNonNullText(root, "payMethod", "method");
        String easy     = firstNonNullText(root, "easyPayProvider");

        return VerifiedPayment.builder()
                .txId(null) // paymentId 조회에서는 txId가 필수가 아님
                .status(normalizeStatus(status))
                .amount(amount)
                .currency(currency)
                .payMethod(method != null ? method.toUpperCase() : null)
                .easyPayProvider(easy)
                .build();
    }

    /**
     * (임시 폴백) V2 결제 단건 조회: txId 기반
     * GET /payments/{txId}
     *  - 일부 계정/환경에서 txId로도 동일 엔드포인트가 조회됨
     *  - 프론트가 paymentId를 안정적으로 보내기 전까지만 사용 권장
     */
    public VerifiedPayment getPaymentByTxId(String txId) {
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

        String status   = text(root, "status");
        Integer amount  = intOrNull(root.at("/amount/total"));
        String currency = text(root.at("/amount/currency"));
        String method   = firstNonNullText(root, "payMethod", "method");
        String easy     = firstNonNullText(root, "easyPayProvider");

        return VerifiedPayment.builder()
                .txId(txId)
                .status(normalizeStatus(status))
                .amount(amount)
                .currency(currency)
                .payMethod(method != null ? method.toUpperCase() : null)
                .easyPayProvider(easy)
                .build();
    }

    // ====== JSON helpers (NPE 방지) ======

    private static String firstNonNullText(JsonNode n, String... fields) {
        if (n == null || fields == null) return null;
        for (String f : fields) {
            if (f == null) continue;
            if (n.has(f) && !n.get(f).isNull()) {
                return n.get(f).asText();
            }
        }
        return null;
    }

    private static String text(JsonNode n, String field) {
        return (n != null && n.has(field) && !n.get(field).isNull()) ? n.get(field).asText() : null;
    }

    private static String text(JsonNode n) {
        return (n != null && !n.isNull()) ? n.asText(null) : null;
    }

    private static Integer intOrNull(JsonNode n) {
        if (n == null || n.isNull()) return null;
        if (n.isInt()) return n.asInt();
        if (n.isNumber()) return n.numberValue().intValue();
        try {
            // 간혹 문자열 숫자로 내려오는 케이스 방어
            String s = n.asText(null);
            return (s != null) ? Integer.parseInt(s) : null;
        } catch (Exception ignore) {
            return null;
        }
    }

    private static String normalizeStatus(String s) {
        if (s == null) return null;
        s = s.trim();

        // 표준화: PortOne V2는 paid/cancelled/refunded/failed 등을 반환
        if ("paid".equalsIgnoreCase(s)) return "PAID";
        if ("cancelled".equalsIgnoreCase(s)) return "CANCELLED";
        if ("refunded".equalsIgnoreCase(s)) return "REFUNDED";
        if ("failed".equalsIgnoreCase(s)) return "FAILED";
        if ("ready".equalsIgnoreCase(s)) return "READY"; // 결제 대기 등 확장

        // 그 외는 대문자 통일
        return s.toUpperCase();
    }
}
