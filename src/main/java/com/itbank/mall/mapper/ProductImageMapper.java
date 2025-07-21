package com.itbank.mall.mapper;

import com.itbank.mall.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductImageMapper {

    // 이미지 1개 추가
    int insert(ProductImage productImage);

    // 특정 상품의 이미지들 모두 조회
    List<ProductImage> findByProductId(Long productId);

    // 특정 상품 이미지 모두 삭제
    int deleteByProductId(Long productId);
}
