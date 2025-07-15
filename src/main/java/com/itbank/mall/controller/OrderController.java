package com.itbank.mall.controller;

import com.itbank.mall.dto.OrderDto;
import com.itbank.mall.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;

    // 생성자 주입
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 주문 내역 조회 페이지
     * ex: /order/list?memberId=1
     */
    @GetMapping("/order/list")
    public String getOrderList(@RequestParam("memberId") Long memberId, Model model) {
        List<OrderDto> orderList = orderService.getOrderListByMemberId(memberId);
        model.addAttribute("orderList", orderList);
        return "order/order_list";  // templates/order/order_list.html
    }
}
