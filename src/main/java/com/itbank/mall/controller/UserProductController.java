package com.itbank.mall.controller;

import com.itbank.mall.entity.Product;
import com.itbank.mall.service.UserProductService;
import com.itbank.mall.service.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/member/products")
public class UserProductController {

    private final UserProductService userProductService;
    private final ProductImageService productImageService;

    public UserProductController(UserProductService userProductService, ProductImageService productImageService) {
        this.userProductService = userProductService;
        this.productImageService = productImageService;
    }

    // ✅ 전체 or 카테고리별 상품 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@RequestParam(name = "categoryId", required = false) Long categoryId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products;

        if (categoryId != null) {
            products = userProductService.getProductsByCategory(categoryId);
        } else {
            products = userProductService.getAllVisibleProducts();
        }

        response.put("code", "SUCCESS");
        response.put("products", products);
        return ResponseEntity.ok(response);
    }

    // ✅ 상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable("id") Long id) {
        Map<String, Object> response = new HashMap<>();

        Product product = userProductService.getVisibleProductById(id);
        if (product == null) {
            response.put("code", "FAIL_NOT_FOUND");
            response.put("message", "상품을 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("code", "SUCCESS");
        response.put("product", product);
        response.put("images", productImageService.getImagesByProductId(id));
        return ResponseEntity.ok(response);
    }
}
