package com.itbank.mall.controller;

import com.itbank.mall.dto.OrderDto;
import com.itbank.mall.security.CustomUserDetails;
import com.itbank.mall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/me")
    public ResponseEntity<List<OrderDto>> getMyOrders(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long memberId = userDetails.getId();
        List<OrderDto> orderList = orderService.getOrderListByMemberId(memberId);
        return ResponseEntity.ok(orderList);
    }
}
