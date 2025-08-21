package com.itbank.mall.mapper.product;

import com.itbank.mall.dto.category.CategoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {

    int insert(CategoryDto dto);  // ✅ DTO로 받기

    int updateName(@Param("id") Long id, @Param("name") String name);

    int delete(@Param("id") Long id);

    List<CategoryDto> findAll();  // ✅ DTO 리스트 반환
}
