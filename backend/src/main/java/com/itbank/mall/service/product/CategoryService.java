package com.itbank.mall.service.product;

import com.itbank.mall.entity.product.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    void addCategory(String name);

    void deleteCategory(Long id);
}
