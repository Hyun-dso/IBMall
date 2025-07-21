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
	public ResponseEntity<?> saveV2Payment(@RequestBody PaymentV2ResponseDto dto, Authentication authentication) {
		Long memberId = null;

		if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
			memberId = userDetails.getId(); // ✅ JWT 기반 memberId 추출
		}

		log.info("💾 [V2 결제 완료] 전달받은 데이터: {}", dto);

		// memberId와 관계 없이 공통으로 검사
		if (dto.getCustomerName() == null || dto.getCustomerName().isBlank() || dto.getCustomerEmail() == null
				|| dto.getCustomerEmail().isBlank() || dto.getCustomerIdentityNumber() == null
				|| dto.getCustomerIdentityNumber().isBlank() || dto.getCustomerAddress() == null
				|| dto.getCustomerAddress().isBlank() || dto.getPgProvider() == null || dto.getPgProvider().isBlank()) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "이름, 이메일, 전화번호, 주소, 결제사는 모두 필수 입력입니다"));
		}

		try {
			String txId = iamportV2Service.saveV2Payment(dto, memberId);

			return ResponseEntity.ok(Map.of("message", "✅ 결제 저장 성공", "transactionId", txId));
		} catch (Exception e) {
			log.error("❌ [결제 저장 실패]", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "결제 저장 실패", "error", e.getMessage()));
		}
	}
}
