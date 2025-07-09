package com.itbank.mall.controller;

import com.itbank.mall.entity.Product;
import com.itbank.mall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/form"; // form.jsp
    }

    @PostMapping("/create")
    public String save(@ModelAttribute Product product) {
        Product saved = productService.save(product);
        return "redirect:/product/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Product product = productService.view(id);
        model.addAttribute("product", product);
        return "product/view"; // view.jsp
    }

    @GetMapping("/timedeals")
    public String timeDeals(Model model) {
        model.addAttribute("list", productService.getTimeDeals());
        return "product/deals"; // deals.jsp
    }
}
