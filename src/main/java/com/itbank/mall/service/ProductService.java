package com.itbank.mall.service;

import com.itbank.mall.entity.Product;
import com.itbank.mall.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class ProductService {

    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<Product> getAllProducts() {
        return productMapper.findAll();
    }

    public Product getProductById(Long productId) {
        return productMapper.findById(productId);
    }

    public void addProduct(Product product) {
    	 if (product.getCreatedAt() == null) {
             product.setCreatedAt(LocalDateTime.now());
        }
    	
    	// 기본값 설정 (DB 제약 오류 방지)
         if (product.getThumbnailUrl() == null) {
             product.setThumbnailUrl("");
         }
         product.setViewCount(product.getViewCount());
         product.setRecommendCount(product.getRecommendCount());
         product.setNotRecommendCount(product.getNotRecommendCount());
         if (product.getTimeSalePrice() == null) {
             product.setTimeSalePrice(null);
         }

         productMapper.insert(product); 
    }

    public void updateProduct(Product product) {
        productMapper.update(product);
    }

    public void deleteProduct(Long productId) {
        productMapper.delete(productId);
    }
}

