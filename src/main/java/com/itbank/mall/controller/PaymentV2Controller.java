package com.itbank.mall.controller;

import com.itbank.mall.dto.PaymentV2ResponseDto;
import com.itbank.mall.security.CustomUserDetails;
import com.itbank.mall.service.IamportV2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/v2")
public class PaymentV2Controller {

    private final IamportV2Service iamportV2Service;

    @PostMapping("/success")
    public ResponseEntity<?> saveV2Payment(@RequestBody PaymentV2ResponseDto dto, Authentication auth) {
        Long memberId = null;

        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            memberId = userDetails.getId();
        }

        log.info("💾 클라이언트로부터 전달받은 결제 완료 데이터: {}", dto);

        // ✅ 비회원일 경우 필수 값 검증
        if (memberId == null) {
            if (dto.getCustomerName() == null || dto.getCustomerEmail() == null ||
                dto.getCustomerIdentityNumber() == null || dto.getPgProvider() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("message", "비회원 결제 시 이름, 이메일, 전화번호는 필수입니다")
                );
            }
        }

        try {
            String txId = iamportV2Service.saveV2Payment(dto, memberId);
            return ResponseEntity.ok(Map.of(
                "message", "결제 저장 성공",
                "transactionId", txId
            ));
        } catch (Exception e) {
            log.error("❌ 결제 저장 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "결제 저장 실패", "error", e.getMessage()));
        }
    }
}
