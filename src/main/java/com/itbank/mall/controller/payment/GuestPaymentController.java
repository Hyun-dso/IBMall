package com.itbank.mall.controller.payment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.payment.GuestPaymentRequestDto;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.payment.GuestPaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments/guest")
@RequiredArgsConstructor
public class GuestPaymentController {

    private final GuestPaymentService guestPaymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> saveGuestPayment(@RequestBody GuestPaymentRequestDto dto) {
        guestPaymentService.processGuestPayment(dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.ok("비회원 결제 및 주문 저장 완료"));
    }
}
