package com.itbank.mall.mapper.product;

import com.itbank.mall.entity.product.OptionType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;   // ✅ 추가 
import java.util.List;

@Mapper
public interface OptionTypeMapper {
    List<OptionType> findAll();
    List<OptionType> findByCategoryId(@Param("categoryId") int categoryId); // ← @Param
    int insert(OptionType optionType);
    int deleteById(@Param("id") int id); // ← @Param
}

