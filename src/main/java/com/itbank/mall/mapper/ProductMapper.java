package com.itbank.mall.mapper;

import com.itbank.mall.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<Product> findAll();
    Product findById(Long productId);
    int insert(Product product);
    int update(Product product);
    int delete(Long productId);

    // ✅ 추가: 상품 상태만 업데이트 (예: ACTIVE, INACTIVE, SOLD_OUT, HIDDEN)
    int updateStatus(@Param("productId") Long productId, @Param("status") String status);
}
