package com.itbank.mall.service.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itbank.mall.dto.category.CategoryDto;
import com.itbank.mall.mapper.product.CategoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    /** 생성 */
    @Override
    @Transactional
    public CategoryDto create(CategoryDto dto) {
        if (dto == null || dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("INVALID_NAME");
        }
        categoryMapper.insert(dto);   // useGeneratedKeys 로 dto.id 채워짐
        return dto;
    }

    /** 전체 조회 */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        return categoryMapper.findAll();
    }

    /** 이름 변경 */
    @Override
    @Transactional
    public void rename(Long id, String newName) {
        if (id == null) throw new IllegalArgumentException("CATEGORY_NOT_FOUND");
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("INVALID_NAME");
        }
        int updated = categoryMapper.updateName(id, newName);
        if (updated == 0) throw new IllegalArgumentException("CATEGORY_NOT_FOUND");
    }

    /** 삭제 */
    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("CATEGORY_NOT_FOUND");
        int deleted = categoryMapper.delete(id);
        if (deleted == 0) throw new IllegalArgumentException("CATEGORY_NOT_FOUND");
    }
}
