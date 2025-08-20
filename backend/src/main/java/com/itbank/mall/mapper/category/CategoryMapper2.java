package com.itbank.mall.mapper.category;

import com.itbank.mall.dto.category.CategoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper2 {
    int insert(CategoryDto dto);                                     // XML <insert>
    int updateName(@Param("id") Long id, @Param("name") String name); // XML <update>
    int delete(@Param("id") Long id);                                 // XML <delete>
    List<CategoryDto> findAll();                                      // XML <select>
}
