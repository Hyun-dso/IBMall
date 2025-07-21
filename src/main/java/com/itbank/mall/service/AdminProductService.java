package com.itbank.mall.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itbank.mall.entity.Product;
import com.itbank.mall.mapper.AdminProductMapper;

@Service
public class AdminProductService {

    private final AdminProductMapper adminProductMapper;

    public AdminProductService(AdminProductMapper productMapper) {
        this.adminProductMapper = productMapper;
    }

    // ✅ 전체 상품 조회 (관리자용)
    public List<Product> getAllProducts() {
        return adminProductMapper.findAll();
    }

    // ✅ 단일 상품 조회 (관리자용)
    public Product getProductById(Long productId) {
        return adminProductMapper.findById(productId);
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

        adminProductMapper.insert(product);
        return product.getProductId();
    }

    // ✅ 상품 수정
    public void updateProduct(Product product) {
    	adminProductMapper.update(product);
    }

    // ✅ 상품 삭제
    public void deleteProduct(Long productId) {
    	adminProductMapper.delete(productId);
    }

    // ✅ 상품 상태만 업데이트 (예: ACTIVE, INACTIVE, SOLD_OUT, HIDDEN)
    public void updateProductStatus(Long productId, String status) {
    	adminProductMapper.updateStatus(productId, status);
    }
    
    // 상품 상태별 목록 확인
    public List<Product> getProductsByStatus(String status) {
        return adminProductMapper.findByStatus(status);
    }
}
