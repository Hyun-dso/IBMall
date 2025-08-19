package com.itbank.mall.controller.admin;

import com.itbank.mall.entity.product.ProductOption;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.product.ProductOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/options")
@RequiredArgsConstructor
public class ProductOptionController {

    private final ProductOptionService productOptionService;

    // ✅ 특정 상품의 옵션 전체 조회
    @GetMapping("/{productId}")
    public ApiResponse<List<ProductOption>> getOptionsByProductId(@PathVariable Long productId) {
        List<ProductOption> list = productOptionService.findByProductId(productId);
        return ApiResponse.ok(list);
    }

    // ✅ 옵션 등록
    @PostMapping
    public ApiResponse<?> addOption(@RequestBody ProductOption option) {
        int result = productOptionService.addOption(option);
        return ApiResponse.ok(result);
    }

    // ✅ 옵션 삭제
    @DeleteMapping("/{optionId}")
    public ApiResponse<?> deleteOption(@PathVariable Long optionId) {
        int result = productOptionService.deleteById(optionId);
        return ApiResponse.ok(result);
    }

    // ✅ 옵션 수정
    @PutMapping
    public ApiResponse<?> updateOption(@RequestBody ProductOption option) {
        int result = productOptionService.updateOption(option);
        return ApiResponse.ok(result);
    }
}