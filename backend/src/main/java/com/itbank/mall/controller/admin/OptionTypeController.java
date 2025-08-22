package com.itbank.mall.controller.admin;

import com.itbank.mall.entity.product.OptionType;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.product.OptionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/option-types")
@RequiredArgsConstructor
public class OptionTypeController {

    private final OptionTypeService optionTypeService;

    // ✅ 전체 옵션 타입 목록 조회
    @GetMapping
    public ApiResponse<List<OptionType>> getAllOptionTypes() {
        return ApiResponse.ok(optionTypeService.findAll());
    }

    // ✅ 옵션 타입 추가
    @PostMapping
    public ApiResponse<?> addOptionType(@RequestBody OptionType type) {
        int result = optionTypeService.addOptionType(type);
        return ApiResponse.ok(result, "옵션 타입이 추가되었습니다");
    }

    // ✅ 옵션 타입 삭제
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteOptionType(@PathVariable int id) {
        int result = optionTypeService.deleteById(id);
        return ApiResponse.ok(result, "옵션 타입이 삭제되었습니다");
    }
}
