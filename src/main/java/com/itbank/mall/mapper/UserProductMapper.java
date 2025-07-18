package com.itbank.mall.mapper;

import com.itbank.mall.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserProductMapper {

    List<Product> findByCategoryId(Long categoryId);    // 카테고리별 조회
    List<Product> findVisibleProducts();               // 노출 상태 ACTIVE만 조회
    Product findVisibleById(Long productId);          // ACTIVE 상태 단일 상품 조회
}
