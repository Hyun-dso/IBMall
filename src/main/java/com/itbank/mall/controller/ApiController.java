package com.itbank.mall.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping("/api/hello")
    public String hello() {
        return "서버에서 온 메시지입니다!";
    }
}
