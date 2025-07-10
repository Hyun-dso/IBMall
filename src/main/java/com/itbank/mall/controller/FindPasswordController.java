package com.itbank.mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FindPasswordController {

    @GetMapping("/find-password")
    public String findPasswordPage() {
        return "find-password";  // → templates/find-password.html
    }
}