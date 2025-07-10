package com.itbank.mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.itbank.mall.entity.Member;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        Member loginUser = (Member) session.getAttribute("loginUser");

        if (loginUser != null) {
            model.addAttribute("nickname", loginUser.getNickname());
        }

        return "home"; // templates/home.html
    }

	@GetMapping("signin")
	public String signin() {
		return "signin";
	}

	@GetMapping("signup")
	public String signup() {
		return "signup";
	}
}
