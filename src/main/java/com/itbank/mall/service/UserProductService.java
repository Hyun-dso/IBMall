package com.itbank.mall.service;

import com.itbank.mall.entity.product.Product;
import com.itbank.mall.mapper.UserProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProductService {

    private final UserProductMapper userProductMapper;

    public UserProductService(UserProductMapper userProductMapper) {
        this.userProductMapper = userProductMapper;
    }

    // ✅ 카테고리별 ACTIVE 상품 조회
    public List<Product> getProductsByCategory(Long categoryId) {
        return userProductMapper.findByCategoryId(categoryId);
    }

    // ✅ 전체 ACTIVE 상품 조회
    public List<Product> getAllVisibleProducts() {
        return userProductMapper.findVisibleProducts();
    }

    // ✅ 단일 ACTIVE 상품 조회
    public Product getVisibleProductById(Long productId) {
        return userProductMapper.findVisibleById(productId);
    }
}
