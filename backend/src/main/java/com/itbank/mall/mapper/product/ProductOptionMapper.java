package com.itbank.mall.mapper.product;

import com.itbank.mall.entity.product.ProductOption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductOptionMapper {

    // 옵션 목록 조회 (상품 ID 기준)
    List<ProductOption> findByProductId(Long productId);

    // 옵션 추가
    int insert(ProductOption option);

    // 옵션 삭제
    int deleteById(Long id);

    // 옵션 수정 ✅ 이거 추가!
    int update(ProductOption option);
}
