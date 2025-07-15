package com.itbank.mall.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.PaymentRequestDto;
import com.itbank.mall.dto.PaymentV2ResponseDto;
import com.itbank.mall.entity.Member;
import com.itbank.mall.service.IamportService;
import com.itbank.mall.service.IamportV2Service;
import com.itbank.mall.service.PaymentService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

    private final IamportService iamportService;       // V1용
    private final IamportV2Service iamportV2Service;   // ✅ V2용
    private final PaymentService paymentService;

    // ✅ V1 아임포트 토큰 발급 테스트
    @GetMapping("/token")
    public Mono<ResponseEntity<String>> getToken() {
        return iamportService.getAccessToken()
                .map(token -> ResponseEntity.ok("AccessToken: " + token))
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.internalServerError()
                                .body("토큰 발급 실패: " + e.getMessage()))
                );
    }

    // ✅ V1 결제 (아임포트 결제창)
    @PostMapping("/pay")
    public Mono<ResponseEntity<String>> pay(@RequestBody PaymentRequestDto dto, HttpSession session) {
        Member login = (Member) session.getAttribute("loginUser");
        if (login == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요"));
        }

        return iamportService.requestOneWonPayment(dto, login.getId())
            .map(txId -> ResponseEntity.ok("✅ V1 결제 성공! TX ID: " + txId))
            .onErrorResume(e ->
                Mono.just(ResponseEntity.internalServerError().body("❌ V1 결제 실패: " + e.getMessage()))
            );
    }

    @PostMapping("/pay-v2/success")
    public ResponseEntity<String> saveV2Payment(@RequestBody PaymentV2ResponseDto dto, HttpSession session) {
        Member login = (Member) session.getAttribute("loginUser");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        log.info("💾 클라이언트로부터 전달받은 결제 완료 데이터: {}", dto);

        try {
            String result = iamportV2Service.saveV2Payment(dto, login.getId());
            return ResponseEntity.ok("✅ 결제 저장 완료! TX ID: " + result);
        } catch (Exception e) {
            log.error("❌ 결제 저장 중 예외 발생", e);
            return ResponseEntity.internalServerError().body("❌ 결제 저장 실패: " + e.getMessage());
        }
    }

}
