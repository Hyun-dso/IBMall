package com.itbank.mall.controller;

import com.itbank.mall.entity.Member;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MypageController {

    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        Member login = (Member) session.getAttribute("loginUser");
        if (login == null) {
            return "redirect:/signin";
        }

        model.addAttribute("member", login);
        return "mypage"; // templates/mypage.html 로 연결됨
    }
}
