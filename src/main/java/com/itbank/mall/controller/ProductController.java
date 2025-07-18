package com.itbank.mall.controller;

import com.itbank.mall.entity.Product;
import com.itbank.mall.entity.ProductImage;
import com.itbank.mall.service.ProductService;
import com.itbank.mall.service.ProductImageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("product")  // ✅ 관리자용 prefix
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    public ProductController(ProductService productService, ProductImageService productImageService) {
        this.productService = productService;
        this.productImageService = productImageService;
    }

    // ✅ 관리자용 상품 목록
    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "list";  // ✅ templates/list.html (관리자용)
    }

    // ✅ 관리자용 상품 상세
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("images", productImageService.getImagesByProductId(id));
        return "detail";  // ✅ templates/detail.html (관리자용)
    }

    // ✅ 상품 추가
    @PostMapping
    public String add(@ModelAttribute Product product,
                      @RequestParam(name = "imageUrls", required = false) String imageUrls) {

        Long productId = productService.addProduct(product);

        if (imageUrls != null && !imageUrls.isBlank()) {
            String[] urls = imageUrls.split(",");
            for (String url : urls) {
                if (url.isBlank()) continue;
                ProductImage img = new ProductImage();
                img.setProductId(productId);
                img.setImageUrl(url.trim());
                productImageService.saveProductImage(img);
            }
        }
        return "redirect:/product";
    }

    // ✅ 상품 수정
    @PostMapping("/update")
    public String update(@ModelAttribute Product product,
                         @RequestParam(name = "imageUrls", required = false) String imageUrls) {

        productService.updateProduct(product);
        productImageService.deleteImagesByProductId(product.getProductId());

        if (imageUrls != null && !imageUrls.isBlank()) {
            String[] urls = imageUrls.split(",");
            for (String url : urls) {
                if (url.isBlank()) continue;
                ProductImage img = new ProductImage();
                img.setProductId(product.getProductId());
                img.setImageUrl(url.trim());
                productImageService.saveProductImage(img);
            }
        }
        return "redirect:/product";
    }

    // ✅ 상품 삭제
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return "redirect:/product";
    }
}
