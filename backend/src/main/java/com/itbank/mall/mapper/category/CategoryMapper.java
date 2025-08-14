package com.itbank.mall.mapper.category;


import com.itbank.mall.dto.category.CategoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    int insert(CategoryDto dto);                        // useGeneratedKeys로 dto.id 채움
    int updateName(@Param("id") Long id, @Param("name") String name);
    int delete(@Param("id") Long id);

    CategoryDto findById(@Param("id") Long id);
    List<CategoryDto> findAll();

    boolean existsById(@Param("id") Long id);
    boolean existsByName(@Param("name") String name);
}
