package com.itbank.mall.controller.common;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.user.product.ProductDetailResponseDto;
import com.itbank.mall.dto.user.product.ProductUserResponseDto;
import com.itbank.mall.entity.product.Product;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.UserProductService;
import com.itbank.mall.service.admin.ProductImageService;
import com.itbank.mall.util.ProductDtoConverter;

@RestController
@RequestMapping("/api/products")
public class UserProductController {

    private final UserProductService userProductService;
    private final ProductImageService productImageService;

    public UserProductController(UserProductService userProductService, ProductImageService productImageService) {
        this.userProductService = userProductService;
        this.productImageService = productImageService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductUserResponseDto>>> list(
            @RequestParam(name = "categoryId", required = false) Long categoryId) {

        List<Product> products = (categoryId != null)
            ? userProductService.getProductsByCategory(categoryId)
            : userProductService.getAllVisibleProducts();

        List<ProductUserResponseDto> dtos = products.stream()
            .map(ProductDtoConverter::toUserResponse)
            .toList();

        return ResponseEntity.ok(ApiResponse.ok(dtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponseDto>> detail(@PathVariable("id") Long id) {
        Product product = userProductService.getVisibleProductById(id);
        if (product == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail("상품을 찾을 수 없습니다."));
        }

        ProductUserResponseDto dto = ProductDtoConverter.toUserResponse(product);
        List<String> imageUrls = productImageService.getImagesByProductId(id)
            .stream()
            .map(img -> img.getImageUrl())
            .toList();

        ProductDetailResponseDto data = new ProductDetailResponseDto(dto, imageUrls);
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
