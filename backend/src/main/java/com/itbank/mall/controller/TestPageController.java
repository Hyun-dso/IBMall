package com.itbank.mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestPageController {

    @GetMapping("/paymenttest")
    public String paymentTestPage() {
        return "paymenttest";  // templates/paymenttest.html
    }
}
