package com.itbank.mall.controller.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.payment.PaymentRequestDto;
import com.itbank.mall.security.CustomUserDetails;
import com.itbank.mall.service.payment.IamportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/v1")
public class PaymentV1Controller {

    private final IamportService iamportService;

    // ✅ 아임포트 V1 토큰 발급
    @GetMapping("/token")
    public Mono<ResponseEntity<String>> getToken() {
        return iamportService.getAccessToken()
                .map(token -> ResponseEntity.ok("AccessToken: " + token))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.internalServerError().body("토큰 발급 실패: " + e.getMessage())
                ));
    }

    // ✅ 아임포트 1원 테스트 결제
    @PostMapping("/pay")
    public Mono<ResponseEntity<String>> pay(@RequestBody PaymentRequestDto dto, Authentication auth) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long memberId = userDetails.getId();

        return iamportService.requestOneWonPayment(dto, memberId)
                .map(txId -> ResponseEntity.ok("✅ V1 결제 성공! TX ID: " + txId))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.internalServerError().body("❌ V1 결제 실패: " + e.getMessage())
                ));
    }
}
