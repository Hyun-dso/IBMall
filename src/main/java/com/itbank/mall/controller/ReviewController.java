package com.itbank.mall.controller;

import com.itbank.mall.entity.Review;
import com.itbank.mall.entity.Product;
import com.itbank.mall.service.ReviewService;
import com.itbank.mall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductService productService;

    // 리뷰 작성 폼
    @GetMapping("/write/{productId}")
    public String writeForm(@PathVariable Long productId, Model model) {
        model.addAttribute("productId", productId);
        model.addAttribute("review", new Review());
        return "review/form";
    }

    // 리뷰 저장
    @PostMapping("/write/{productId}")
    public String save(@PathVariable Long productId, @ModelAttribute Review review) {
        Product product = productService.viewProduct(productId); // 상품 조회
        review.setProduct(product);
        reviewService.save(review);
        return "redirect:/product/" + productId;
    }

    // 리뷰 목록
    @GetMapping("/list/{productId}")
    public String reviewList(@PathVariable Long productId, Model model) {
        List<Review> reviews = reviewService.findByProductId(productId);
        model.addAttribute("reviews", reviews);
        return "review/list";
    }
}
