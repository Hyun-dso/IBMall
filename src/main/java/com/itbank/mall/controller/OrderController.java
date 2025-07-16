package com.itbank.mall.controller;

import com.itbank.mall.dto.OrderDto;
import com.itbank.mall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{memberId}")
    public ResponseEntity<List<OrderDto>> getOrdersByMemberId(@PathVariable Long memberId) {
        List<OrderDto> orderList = orderService.getOrderListByMemberId(memberId);
        return ResponseEntity.ok(orderList);
    }
}