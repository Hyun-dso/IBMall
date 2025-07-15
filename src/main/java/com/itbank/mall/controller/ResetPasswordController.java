package com.itbank.mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResetPasswordController {

	@GetMapping("/reset-password")
	public String resetPasswordPage(@RequestParam("token") String token, Model model) {
	    model.addAttribute("token", token);
	    return "reset-password";
	}
}
