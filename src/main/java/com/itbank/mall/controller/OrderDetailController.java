package com.itbank.mall.controller;

import com.itbank.mall.dto.OrderDetailDto;
import com.itbank.mall.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-detail")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    /**
     * 주문 상세 정보 JSON 반환 (단일 DTO로 구성)
     * @param orderId 주문 번호
     * @return OrderDetailDto (주문 + 상품 + 배송 정보)
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(@PathVariable ("orderId")Long orderId) {
        OrderDetailDto dto = orderDetailService.getOrderDetailById(orderId);
        return ResponseEntity.ok(dto);
    }
}
