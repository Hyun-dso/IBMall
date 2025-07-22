package com.itbank.mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.itbank.mall.dto.oauth.TempUserDto;
import com.itbank.mall.entity.member.Member;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home() {
	    return "home";  // nickname은 JS가 fetch로 직접 받아옴
	}

	@GetMapping("signin")
	public String signin() {
		return "signin";
	}

	@GetMapping("signup")
	public String signup() {
		return "signup";
	}
	
	@GetMapping("/signup-by-google")
	public String signupByGooglePage(HttpSession session, Model model) {
	    TempUserDto tempUser = (TempUserDto) session.getAttribute("tempGoogleUser");

	    if (tempUser == null) {
	        return "redirect:/";
	    }

	    model.addAttribute("email", tempUser.getEmail());
	    return "signup-by-google";  // → templates/signup-by-google.html
	}
	
    @GetMapping("/payment-v2")
    public String paymentV2Page() {
        return "payment-v2";  // → templates/payment-v2.html 로 연결
    }
}
