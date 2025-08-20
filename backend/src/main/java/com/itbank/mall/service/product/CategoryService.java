package com.itbank.mall.service.product;

import java.util.List;

import com.itbank.mall.dto.category.CategoryDto;

public interface CategoryService {

    List<CategoryDto> findAll();
    CategoryDto create(CategoryDto dto);
    void rename(Long id, String newName);
    void delete(Long id); 
}
