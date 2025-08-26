package com.itbank.mall.controller.orders;

import com.itbank.mall.dto.orders.OrderDetailDto;
import com.itbank.mall.service.orders.OrderDetailService;

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
     * @param Uid 주문 고유 UID
     * @return OrderDetailDto (주문 + 상품 + 배송 정보)
     */
    @GetMapping("/{orderUid}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(@PathVariable("orderUid") String orderUid) {
        OrderDetailDto dto = orderDetailService.getOrderDetailByUid(orderUid);
        return ResponseEntity.ok(dto);
    }
}
