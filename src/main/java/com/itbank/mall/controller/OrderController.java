package com.itbank.mall.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.OrderDetailDto;
import com.itbank.mall.entity.Order;
import com.itbank.mall.service.OrderService;

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

	@GetMapping("/order-detail/{orderId}")
	public ResponseEntity<OrderDetailDto> getOrderDetail(@PathVariable Long orderId) {
		OrderDetailDto detail = orderService.getOrderDetail(orderId);
		return ResponseEntity.ok(detail);
	}
}