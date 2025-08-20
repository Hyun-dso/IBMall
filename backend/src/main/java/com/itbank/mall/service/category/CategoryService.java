package com.itbank.mall.service.category;

import com.itbank.mall.dto.category.CategoryDto;
import com.itbank.mall.mapper.product.CategoryMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    /** 생성 */
    @Transactional
    public CategoryDto create(CategoryDto dto) {
        if (dto == null || dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("INVALID_NAME");
        }
        categoryMapper.insert(dto); // useGeneratedKeys 로 dto.id 채워짐
        return dto;
    }

    /** 전체 조회 */
    @Transactional(readOnly = true)
    public List<CategoryDto> list() {
        return categoryMapper.findAll();
    }

    /** 이름 변경 */
    @Transactional
    public void rename(Long id, CategoryDto dto) {
        if (id == null) throw new IllegalArgumentException("CATEGORY_NOT_FOUND");
        String newName = (dto != null) ? dto.getName() : null;
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("INVALID_NAME");
        }
        int updated = categoryMapper.updateName(id, newName);
        if (updated == 0) throw new IllegalArgumentException("CATEGORY_NOT_FOUND");
    }

    /** 삭제 */
    @Transactional
    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("CATEGORY_NOT_FOUND");
        int deleted = categoryMapper.delete(id);
        if (deleted == 0) throw new IllegalArgumentException("CATEGORY_NOT_FOUND");
    }
}
