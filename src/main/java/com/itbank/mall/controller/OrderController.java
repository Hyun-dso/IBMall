package com.itbank.mall.controller;

import com.itbank.mall.entity.Order;
import com.itbank.mall.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{memberId}")
    public List<Order> getOrdersByMemberId(@PathVariable("memberId") Long memberId) {
        return orderService.getOrdersByMemberId(memberId);
    }


}
