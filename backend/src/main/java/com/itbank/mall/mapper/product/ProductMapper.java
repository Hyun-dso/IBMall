// com.itbank.mall.mapper.product.ProductMapper
package com.itbank.mall.mapper.product;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper {
    int decreaseStock(@Param("productId") Long productId,
                      @Param("qty") int qty);

    int getPriceById(@Param("productId") Long productId);
}
