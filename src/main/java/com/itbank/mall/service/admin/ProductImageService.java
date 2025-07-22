package com.itbank.mall.service.admin;

import com.itbank.mall.entity.product.ProductImage;
import com.itbank.mall.mapper.admin.ProductImageMapper;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageService {

    private final ProductImageMapper productImageMapper;

    public ProductImageService(ProductImageMapper productImageMapper) {
        this.productImageMapper = productImageMapper;
    }

    // 이미지 1개 저장
    public void saveProductImage(ProductImage productImage) {
        productImageMapper.insert(productImage);
    }

    // 특정 상품의 이미지들 조회
    public List<ProductImage> getImagesByProductId(Long productId) {
        return productImageMapper.findByProductId(productId);
    }

   // 특정 상품의 이미지 모두 삭제 후 개수 반환
    public int deleteImagesByProductId(Long productId) {
       return productImageMapper.deleteByProductId(productId);
   }
}
