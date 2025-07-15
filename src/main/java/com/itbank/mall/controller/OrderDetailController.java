package com.itbank.mall.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itbank.mall.dto.OrderDetailDto;
import com.itbank.mall.service.OrderDetailService;

@Controller
@RequestMapping("/order")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @Autowired
    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("orderId") Long orderId, Model model) {
        OrderDetailDto dto = orderDetailService.getOrderDetailById(orderId);  // ✅ 단일 DTO

        // 🔁 테이블 반복 출력을 위해 리스트로 감싼다
        List<OrderDetailDto> orderDetailList = new ArrayList<>();
        orderDetailList.add(dto);

        // 🔑 HTML에서 쓰는 이름에 맞춰서 model에 담는다
        model.addAttribute("orderDetailList", orderDetailList);
        model.addAttribute("orderId", dto.getOrderId());
        model.addAttribute("createdAt", dto.getCreatedAt());
        model.addAttribute("orderStatus", dto.getOrderStatus());

        // 배송 정보는 Map으로 따로 묶어서 넘긴다
        Map<String, Object> delivery = new HashMap<>();
        delivery.put("recipient", dto.getRecipient());
        delivery.put("phone", dto.getPhone());
        delivery.put("deliveryAddress", dto.getDeliveryAddress());
        delivery.put("trackingNumber", dto.getTrackingNumber());
        delivery.put("deliveryStatus", dto.getDeliveryStatus());
        model.addAttribute("delivery", delivery);

        return "order/order_detail";  // 🔁 HTML 파일 경로
    }
}