package com.itbank.mall.mapper.product;

import com.itbank.mall.entity.product.ProductOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductOptionMapper {
    List<ProductOption> findByProductId(@Param("productId") Long productId); // ← @Param
    int decreaseStock(@Param("optionId") Long optionId,
            @Param("qty") int qty);

int getExtraPriceById(@Param("optionId") Long optionId);
    int insert(ProductOption option);   // 객체라 @Param 불필요
    int deleteById(@Param("id") Long id); // ← @Param
    int update(ProductOption option);   // 객체라 @Param 불필요
}