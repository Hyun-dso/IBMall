package com.itbank.mall.controller;

import com.itbank.mall.dto.ProductRequestDTO;
import com.itbank.mall.entity.Product;
import com.itbank.mall.entity.ProductImage;
import com.itbank.mall.service.ProductService;
import com.itbank.mall.service.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/products")
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    public ProductController(ProductService productService, ProductImageService productImageService) {
        this.productService = productService;
        this.productImageService = productImageService;
    }

    // ✅ 상품 목록
    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", "SUCCESS");
        response.put("products", productService.getAllProducts());
        return ResponseEntity.ok(response);
    }

    // ✅ 상품 상세
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Product product = productService.getProductById(id);
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

    // ✅ 상품 추가
    @PostMapping
    public ResponseEntity<Map<String, Object>> add(@RequestBody ProductRequestDTO productRequest) {
        Map<String, Object> response = new HashMap<>();
        Long productId = productService.addProduct(productRequest.toProduct());

        if (productRequest.getImageUrls() != null) {
            for (String url : productRequest.getImageUrls()) {
                ProductImage img = new ProductImage();
                img.setProductId(productId);
                img.setImageUrl(url);
                productImageService.saveProductImage(img);
            }
        }

        response.put("code", "SUCCESS");
        response.put("productId", productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ 상품 수정
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody ProductRequestDTO productRequest) {
        Map<String, Object> response = new HashMap<>();

        productRequest.setProductId(id);
        productService.updateProduct(productRequest.toProduct());
        productImageService.deleteImagesByProductId(id);

        if (productRequest.getImageUrls() != null) {
            for (String url : productRequest.getImageUrls()) {
                ProductImage img = new ProductImage();
                img.setProductId(id);
                img.setImageUrl(url);
                productImageService.saveProductImage(img);
            }
        }

        response.put("code", "SUCCESS");
        return ResponseEntity.ok(response);
    }

    // ✅ 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        productService.deleteProduct(id);
        response.put("code", "SUCCESS");
        return ResponseEntity.ok(response);
    }

    // ✅ 상품 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        productService.updateProductStatus(id, status);
        response.put("code", "SUCCESS");
        return ResponseEntity.ok(response);
    }
}
