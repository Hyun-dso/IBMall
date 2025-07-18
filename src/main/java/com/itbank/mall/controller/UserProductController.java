package com.itbank.mall.controller;

import com.itbank.mall.entity.Product;
import com.itbank.mall.service.UserProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/shop/product")  // ✅ 사용자용 prefix
public class UserProductController {

    private final UserProductService userProductService;

    public UserProductController(UserProductService userProductService) {
        this.userProductService = userProductService;
    }

    // ✅ 사용자용 상품 목록 페이지
    @GetMapping
    public String list(@RequestParam(name = "categoryId", required = false) Long categoryId, Model model) {
        List<Product> products;

        if (categoryId != null) {
            products = userProductService.getProductsByCategory(categoryId);
        } else {
            products = userProductService.getAllVisibleProducts();
        }

        model.addAttribute("products", products);
        return "shop/list";  // templates/shop/list.html
    }

    // ✅ 사용자용 상품 상세 페이지
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Product product = userProductService.getVisibleProductById(id);
        if (product == null) {
            return "redirect:/shop/product";
        }

        model.addAttribute("product", product);
        return "shop/detail";  // templates/shop/detail.html
    }
}
