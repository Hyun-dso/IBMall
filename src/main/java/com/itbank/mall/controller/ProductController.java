package com.itbank.mall.controller;

import com.itbank.mall.entity.Product;
import com.itbank.mall.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "list";  // templates/list.html
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "detail";  // templates/detail.html
    }

    @PostMapping
    public String add(@ModelAttribute Product product) {
        productService.addProduct(product);
        return "redirect:/product";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Product product) {
        productService.updateProduct(product);
        return "redirect:/product";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return "redirect:/product";
    }
}
