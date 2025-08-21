package com.itbank.mall.controller.admin;


import com.itbank.mall.dto.category.CategoryDto;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class CategoryController {

 private final CategoryService service;

 // 생성
 @PostMapping
 public ApiResponse<CategoryDto> create(@RequestBody CategoryDto dto) {
     return ApiResponse.ok(service.create(dto), "카테고리 생성 완료");
 }

 // 전체 조회
 @GetMapping
 public ApiResponse<List<CategoryDto>> list() {
     return ApiResponse.ok(service.list());
 }

 // 이름 변경
 @PutMapping("/{id}")
 public ApiResponse<String> rename(@PathVariable("id") Long id,
                                   @RequestBody CategoryDto dto) {
     service.rename(id, dto);
     return ApiResponse.ok(null, "수정 완료");
 }

 // 삭제
 @DeleteMapping("/{id}")
 public ApiResponse<String> delete(@PathVariable("id") Long id) {
     service.delete(id);
     return ApiResponse.ok(null, "삭제 완료");
 }
}
