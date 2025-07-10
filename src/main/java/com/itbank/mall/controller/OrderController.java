package com.itbank.mall.controller;

import com.itbank.mall.dto.OrderDetailDto;
import com.itbank.mall.entity.Order;
import com.itbank.mall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")  // 복수형 RESTful 경로
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
    	 System.out.println("🚀 OrderController 로딩됨");
        this.orderService = orderService;
    }

    // ✅ 회원별 주문 목록: GET /orders/member/{memberId}
    @GetMapping("/member/{memberId}")
    public String getOrderList(@PathVariable("memberId") Long memberId, Model model) {
        List<Order> orderList = orderService.getOrdersByMemberId(memberId);
        model.addAttribute("orderList", orderList);
        return "order_list";  // templates/order_list.html
    }

    // ✅ 주문 상세 조회: GET /orders/{id}
    @GetMapping("/{id}")
    public String getOrderDetail(@PathVariable("id") Long id, Model model) {
    	System.out.println("🔥 주문 상세 컨트롤러 진입: " + id);
        OrderDetailDto dto = orderService.getOrderDetail(id);
        if (dto == null) {
            return "error/404";  // or redirect:/orders or custom page
        }
        model.addAttribute("order", dto);
        return "order_detail";  // templates/order_detail.html
    }
    
    
}
