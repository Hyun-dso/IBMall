package com.itbank.mall.service;

import com.itbank.mall.entity.Product;
import com.itbank.mall.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    // ✅ 전체 상품 조회 (관리자용)
    public List<Product> getAllProducts() {
        return productMapper.findAll();
    }

    // ✅ 단일 상품 조회 (관리자용)
    public Product getProductById(Long productId) {
        return productMapper.findById(productId);
    }

    // ✅ 상품 추가
    public Long addProduct(Product product) {
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(LocalDateTime.now());
        }
        if (product.getThumbnailUrl() == null) {
            product.setThumbnailUrl("");
        }
        if (product.getViewCount() == null) {
            product.setViewCount(0);
        }
        if (product.getRecommendCount() == null) {
            product.setRecommendCount(0);
        }
        if (product.getNotRecommendCount() == null) {
            product.setNotRecommendCount(0);
        }

        productMapper.insert(product);
        return product.getProductId();
    }

    // ✅ 상품 수정
    public void updateProduct(Product product) {
        productMapper.update(product);
    }

    // ✅ 상품 삭제
    public void deleteProduct(Long productId) {
        productMapper.delete(productId);
    }
}
