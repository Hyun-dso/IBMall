package com.itbank.mall.controller.common;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.common.email.EmailCodeVerifyRequestDto;
import com.itbank.mall.dto.common.email.EmailVerificationRequestDto;
import com.itbank.mall.exception.EmailAlreadyExistsException;
import com.itbank.mall.exception.EmailCooldownException;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.common.EmailVerificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

	private final EmailVerificationService emailVerificationService;

	// 변경: ApiResponse 사용 & 상태코드 세분화(400/409/429/500)
	@PostMapping("/code/send")
	public ResponseEntity<ApiResponse<Void>> sendCode(@RequestBody EmailVerificationRequestDto dto) {
		final String raw = dto.getEmail();
		try {
			emailVerificationService.sendVerificationCodeForSignup(raw);
			return ResponseEntity.ok(ApiResponse.ok(null, "인증코드를 전송했습니다."));
		} catch (IllegalArgumentException e) { // 형식 오류 등
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(e.getMessage()));
		} catch (DuplicateKeyException | EmailAlreadyExistsException e) { // 409
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.fail(e.getMessage()));
		} catch (EmailCooldownException e) { // 429
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ApiResponse.fail(e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.fail("이메일 전송 실패"));
		}
	}

	@PostMapping("/code/verify")
	public ResponseEntity<ApiResponse<Void>> verify(@RequestBody EmailCodeVerifyRequestDto dto) {
		try {
			boolean success = emailVerificationService.verifyCode(dto.getEmail(), dto.getCode());
			if (success) {
				return ResponseEntity.ok(ApiResponse.ok(null, "이메일 인증 성공"));
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail("인증 실패 또는 만료된 코드입니다"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.fail("서버 오류가 발생했습니다."));
		}
	}
}
