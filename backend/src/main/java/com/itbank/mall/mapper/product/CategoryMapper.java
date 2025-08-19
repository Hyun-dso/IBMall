package com.itbank.mall.mapper.product;

import com.itbank.mall.entity.product.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    // 전체 카테고리 조회
    List<Category> findAll();

    // 카테고리 추가
    int insert(Category category);

    // 카테고리 삭제
    int deleteById(Long id);
}

