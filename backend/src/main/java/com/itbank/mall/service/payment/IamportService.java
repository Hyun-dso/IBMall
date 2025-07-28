package com.itbank.mall.service.payment;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.itbank.mall.config.IamportProperties;
import com.itbank.mall.dto.payment.PaymentRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class IamportService {

    private final IamportProperties properties;
    private final PaymentService paymentService;

    public Mono<String> getAccessToken() {
        WebClient client = WebClient.builder()
                .baseUrl("https://api.iamport.kr")
                .build();

        return client.post()
                .uri("/users/getToken")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "imp_key", properties.getApiKey(),
                        "imp_secret", properties.getApiSecret()
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Object token = ((Map<?, ?>) response.get("response")).get("access_token");
                    return token != null ? token.toString() : null;
                });
    }
    
    public Mono<String> requestOneWonPayment(PaymentRequestDto dto, Long memberId) {
        // 아임포트 결제 요청 후, transactionId 받아왔다고 가정하면:
        return getAccessToken()
            .flatMap(token -> {
                WebClient client = WebClient.create("https://api.iamport.kr");

                return client.post()
                    .uri("/payments/prepare")
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                        "merchant_uid", dto.getMerchantUid(),
                        "amount", dto.getAmount()
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(result -> {
                        String transactionId = dto.getMerchantUid(); // 예시로 사용
                        paymentService.savePaymentLog(dto, transactionId, memberId);
                        return transactionId;
                    });
            });
    }
}
