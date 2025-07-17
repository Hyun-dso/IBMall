package com.itbank.mall.controller;

import com.itbank.mall.entity.Product;
import com.itbank.mall.entity.ProductImage;
import com.itbank.mall.service.ProductService;
import com.itbank.mall.service.ProductImageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    public ProductController(ProductService productService, ProductImageService productImageService) {
        this.productService = productService;
        this.productImageService = productImageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "list";  // templates/list.html
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("images", productImageService.getImagesByProductId(id));
        return "detail";  // templates/detail.html
    }

    @PostMapping
    public String add(@ModelAttribute Product product,
                      @RequestParam(name = "imageUrls", required = false) String imageUrls) {

        Long productId = productService.addProduct(product);

        System.out.println("[ADD] imageUrls: " + imageUrls);

        if (imageUrls != null && !imageUrls.isBlank()) {
            String[] urls = imageUrls.split(",");
            System.out.println("[ADD] Parsed urls count: " + urls.length);

            for (String url : urls) {
                if (url.isBlank()) continue;
                System.out.println("[ADD] Saving image: " + url.trim());

                ProductImage img = new ProductImage();
                img.setProductId(productId);
                img.setImageUrl(url.trim());
                productImageService.saveProductImage(img);
            }
        }
        return "redirect:/product";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Product product,
                         @RequestParam(name = "imageUrls", required = false) String imageUrls) {

        productService.updateProduct(product);
        System.out.println("[UPDATE] imageUrls: " + imageUrls);

        // 기존 이미지 삭제
        productImageService.deleteImagesByProductId(product.getProductId());
        System.out.println("[UPDATE] Existing images deleted for productId: " + product.getProductId());

        if (imageUrls != null && !imageUrls.isBlank()) {
            String[] urls = imageUrls.split(",");
            System.out.println("[UPDATE] Parsed urls count: " + urls.length);

            for (String url : urls) {
                if (url.isBlank()) continue;
                System.out.println("[UPDATE] Saving image: " + url.trim());

                ProductImage img = new ProductImage();
                img.setProductId(product.getProductId());
                img.setImageUrl(url.trim());
                productImageService.saveProductImage(img);
            }
        }
        return "redirect:/product";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        System.out.println("[DELETE] Product deleted: " + id);
        return "redirect:/product";
    }
}
