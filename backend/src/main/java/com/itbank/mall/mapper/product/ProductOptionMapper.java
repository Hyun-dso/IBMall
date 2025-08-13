// com.itbank.mall.mapper.product.ProductOptionMapper
package com.itbank.mall.mapper.product;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductOptionMapper {
    int decreaseStock(@Param("optionId") Long optionId,
                      @Param("qty") int qty);

    int getExtraPriceById(@Param("optionId") Long optionId);
}
