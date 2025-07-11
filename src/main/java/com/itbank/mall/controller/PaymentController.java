package com.itbank.mall.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.PaymentRequestDto;
import com.itbank.mall.entity.Payment;
import com.itbank.mall.service.IamportService;
import com.itbank.mall.service.PaymentService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class PaymentController {

	   private final IamportService iamportService;
	   private final PaymentService paymentService;

    // 1. 아임포트 토큰 발급 테스트
    @GetMapping("/api/token")
    public Mono<ResponseEntity<String>> getToken() {
        return iamportService.getAccessToken()
                .map(token -> ResponseEntity.ok("AccessToken: " + token))
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.internalServerError()
                                .body("토큰 발급 실패: " + e.getMessage()))
                );
    }
    
    @PostMapping("/pay")
    public Mono<ResponseEntity<String>> pay(@RequestBody PaymentRequestDto dto) {
        return iamportService.requestOneWonPayment(dto)
            .map(result -> {
                // 1. 결제 성공 후 DB에 저장
                Payment payment = new Payment();
                payment.setMemberId(1L);  // TODO: 실제 로그인 세션 값으로 바꿔야 함
                payment.setOrderId(1L);   // TODO: 주문 흐름 정해지면 바꿔야 함
                payment.setAmount(dto.getAmount());
                payment.setStatus("paid");
                payment.setPaymentMethod("card");
                payment.setTransactionId("test-transaction-id"); // 실제 결제 응답값에서 꺼내면 더 좋음
                payment.setCreatedAt(LocalDateTime.now());

                paymentService.savePayment(payment);

                return ResponseEntity.ok("결제요청 성공 + DB 저장 완료");
            })
            .onErrorResume(e ->
                Mono.just(ResponseEntity.internalServerError().body("결제 실패: " + e.getMessage()))
            );
    }
}
