package com.itbank.mall.service.category;

import com.itbank.mall.dto.category.CategoryDto;
import com.itbank.mall.mapper.category.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

 private final CategoryMapper mapper;

 @Transactional
 public CategoryDto create(CategoryDto dto) {
     if (dto.getName() == null || dto.getName().isBlank())
         throw new IllegalArgumentException("카테고리명을 입력하세요");
     String name = dto.getName().trim();
     if (mapper.existsByName(name))
         throw new IllegalArgumentException("이미 존재하는 카테고리명입니다");

     dto.setName(name);
     mapper.insert(dto);   // useGeneratedKeys → dto.id 세팅됨
     return new CategoryDto(dto.getId(), dto.getName());
 }

 @Transactional(readOnly = true)
 public List<CategoryDto> list() {
     return mapper.findAll();
 }

 @Transactional
 public void rename(Long id, CategoryDto dto) {
     if (!mapper.existsById(id)) throw new IllegalArgumentException("CATEGORY_NOT_FOUND");
     if (dto.getName() == null || dto.getName().isBlank())
         throw new IllegalArgumentException("카테고리명을 입력하세요");
     mapper.updateName(id, dto.getName().trim());
 }

 @Transactional
 public void delete(Long id) {
     int rows = mapper.delete(id);
     if (rows == 0) throw new IllegalArgumentException("CATEGORY_NOT_FOUND");
     // FK(product.category_id) 참조 중이면 DB에서 무결성 예외 발생 가능
 }

 @Transactional(readOnly = true)
 public boolean existsById(Long id) {
     return mapper.existsById(id);
 }
}
