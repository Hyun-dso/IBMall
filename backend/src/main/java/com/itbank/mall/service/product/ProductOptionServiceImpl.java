package com.itbank.mall.service.product;

import com.itbank.mall.entity.product.ProductOption;
import com.itbank.mall.mapper.product.ProductOptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductOptionServiceImpl implements ProductOptionService {

    private final ProductOptionMapper productOptionMapper;

    // ✅ 전체 조회
    @Override
    public List<ProductOption> findByProductId(Long productId) {
        return productOptionMapper.findByProductId(productId);
    }

    // ✅ 옵션 등록
    @Override
    public int addOption(ProductOption option) {
        return productOptionMapper.insert(option);
    }

    // ✅ 옵션 삭제
    @Override
    public int deleteById(Long optionId) {
        return productOptionMapper.deleteById(optionId);
    }

    // ✅ 옵션 수정
    @Override
    public int updateOption(ProductOption option) {
        return productOptionMapper.update(option);
    }
}

