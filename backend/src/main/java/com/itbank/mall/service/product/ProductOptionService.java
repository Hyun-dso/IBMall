package com.itbank.mall.service.product;

import com.itbank.mall.entity.product.ProductOption;

import java.util.List;

public interface ProductOptionService {

    // 특정 상품에 대한 옵션 전체 조회
    List<ProductOption> findByProductId(Long productId);

    // 옵션 1개 등록
    int addOption(ProductOption option);

    // 옵션 1개 삭제
    int deleteById(Long optionId);

    // 옵션 수정
    int updateOption(ProductOption option);
}
