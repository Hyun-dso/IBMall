package com.itbank.mall.controller.payment;

import com.itbank.mall.dto.payment.V2PaymentLookupResponseDto;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.payment.V2PaymentLookupService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class V2PaymentResultController {

    private final V2PaymentLookupService v2PaymentLookupService;

    @GetMapping("/v2-result")
    public ApiResponse<V2PaymentLookupResponseDto> getV2PaymentResult(@RequestParam String txId) {
        V2PaymentLookupResponseDto result = v2PaymentLookupService.getPaymentResultByTxId(txId);
        return ApiResponse.ok(result, "결제 조회 성공");
    }
}
