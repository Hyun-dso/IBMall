	package com.itbank.mall.controller.payment;
	
	import org.springframework.http.HttpStatus;
	import org.springframework.http.ResponseEntity;
	import org.springframework.web.bind.annotation.*;
	
	import com.itbank.mall.dto.payment.GuestPaymentRequestDto;
	import com.itbank.mall.dto.payment.GuestCartPaymentRequestDto;
	import com.itbank.mall.response.ApiResponse;
	import com.itbank.mall.service.payment.GuestPaymentService;
	
	import lombok.RequiredArgsConstructor;
	
	@RestController
	@RequestMapping("/api/payments/guest")
	@RequiredArgsConstructor
	public class GuestPaymentController {
	
	    private final GuestPaymentService guestPaymentService;
	
	    // ✅ 1. 단일 상품 결제
	    @PostMapping("/single")
	    public ResponseEntity<ApiResponse<String>> saveGuestSinglePayment(@RequestBody GuestPaymentRequestDto dto) {
	        guestPaymentService.processGuestPayment(dto);
	        return ResponseEntity
	            .status(HttpStatus.CREATED)
	            .body(ApiResponse.ok("비회원 단일 결제 및 주문 저장 완료"));
	    }
	
	    // ✅ 2. 장바구니 결제
	    @PostMapping("/cart")
	    public ResponseEntity<ApiResponse<String>> saveGuestCartPayment(@RequestBody GuestCartPaymentRequestDto dto) {
	        guestPaymentService.processGuestCartPayment(dto);  // 다음 단계에서 만들 메서드
	        return ResponseEntity
	            .status(HttpStatus.CREATED)
	            .body(ApiResponse.ok("비회원 장바구니 결제 및 주문 저장 완료"));
	    }
	}
