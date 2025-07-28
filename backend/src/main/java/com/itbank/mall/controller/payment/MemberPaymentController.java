package com.itbank.mall.controller.payment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.payment.MemberPaymentRequestDto;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.payment.MemberPaymentService;
import com.itbank.mall.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments/member")
@RequiredArgsConstructor
public class MemberPaymentController {

    private final MemberPaymentService memberPaymentService;
    private final JwtUtil jwtUtil;

    @PostMapping("/single")
    public ResponseEntity<ApiResponse<String>> processSinglePayment(
            @RequestBody MemberPaymentRequestDto dto,
            HttpServletRequest request
    ) {
        // JWT에서 memberId 추출
        String token = jwtUtil.resolveToken(request);
        Long memberId = jwtUtil.getMemberId(token);

        memberPaymentService.processSinglePayment(dto, memberId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("회원 단일 결제 및 주문 저장 완료"));
    }
}
