package com.itbank.mall.controller;

import com.itbank.mall.entity.Product;
import com.itbank.mall.service.UserProductService;
import com.itbank.mall.service.ProductImageService;  // ✅ 요거 추가!!!
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/shop/product")
public class UserProductController {

    private final UserProductService userProductService;
    private final ProductImageService productImageService;  // ✅ 추가

    public UserProductController(UserProductService userProductService, ProductImageService productImageService) {
        this.userProductService = userProductService;
        this.productImageService = productImageService;  // ✅ 주입
    }

    @GetMapping
    public String list(@RequestParam(name = "categoryId", required = false) Long categoryId, Model model) {
        List<Product> products;

        if (categoryId != null) {
            products = userProductService.getProductsByCategory(categoryId);
        } else {
            products = userProductService.getAllVisibleProducts();
        }

        model.addAttribute("products", products);
        return "shop/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
    	System.out.println("상세페이지 id: " + id);  // 로그로 확인
    	
        Product product = userProductService.getVisibleProductById(id);
        if (product == null) {
            return "redirect:/shop/product";
        }

        model.addAttribute("product", product);
        model.addAttribute("images", productImageService.getImagesByProductId(id));
        return "shop/detail";
    }
}



    
    
