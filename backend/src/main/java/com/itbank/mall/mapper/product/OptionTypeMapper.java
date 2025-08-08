package com.itbank.mall.mapper.product;

import com.itbank.mall.entity.product.OptionType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OptionTypeMapper {
    List<OptionType> findByCategoryId(int categoryId);
    int insert(OptionType optionType);
    int deleteById(int id);
}
