package com.itbank.mall.mapper;

import com.itbank.mall.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ProductMapper {

    List<Product> findAll();
    Product findById(Long productId);
    int insert(Product product);
    int update(Product product);
    int delete(Long productId);
}
