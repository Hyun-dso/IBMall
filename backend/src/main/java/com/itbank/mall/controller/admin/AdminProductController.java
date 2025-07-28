package com.itbank.mall.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itbank.mall.dto.admin.product.ProductAdminResponseDto;
import com.itbank.mall.dto.admin.product.ProductRequestDto;
import com.itbank.mall.entity.product.Product;
import com.itbank.mall.entity.product.ProductImage;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.admin.AdminProductService;
import com.itbank.mall.service.admin.ProductImageService;
import com.itbank.mall.util.ProductDtoConverter;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final ProductImageService productImageService;

    public AdminProductController(AdminProductService adminProductService, ProductImageService productImageService) {
        this.adminProductService = adminProductService;
        this.productImageService = productImageService;
    }

    // ✅ 상품 목록
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductAdminResponseDto>>> list() {
        List<Product> products = adminProductService.getAllProducts();

        List<ProductAdminResponseDto> dtos = products.stream()
            .map(p -> ProductDtoConverter.toAdminResponse(
                p,
                productImageService.getImagesByProductId(p.getProductId())
                                   .stream()
                                   .map(ProductImage::getImageUrl)
                                   .toList()
            ))
            .toList();

        return ResponseEntity.ok(ApiResponse.ok(dtos));
    }

    // ✅ 상품 상세
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductAdminResponseDto>> detail(@PathVariable("id") Long id) {
        Product product = adminProductService.getProductById(id);
        if (product == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail("상품을 찾을 수 없습니다."));
        }

        List<String> imageUrls = productImageService.getImagesByProductId(id)
            .stream()
            .map(ProductImage::getImageUrl)
            .toList();

        ProductAdminResponseDto dto = ProductDtoConverter.toAdminResponse(product, imageUrls);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    // ✅ 상품 추가
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> add(@RequestBody ProductRequestDto productRequest) {
        Long productId = adminProductService.addProduct(productRequest.toProduct());

        if (productRequest.getImageUrls() != null) {
            for (String url : productRequest.getImageUrls()) {
                ProductImage img = new ProductImage();
                img.setProductId(productId);
                img.setImageUrl(url);
                productImageService.saveProductImage(img);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(productId, "상품이 등록되었습니다."));
    }

    // ✅ 상품 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(@PathVariable("id") Long id, @RequestBody ProductRequestDto productRequest) {
        productRequest.setProductId(id);
        adminProductService.updateProduct(productRequest.toProduct());
        productImageService.deleteImagesByProductId(id);

        if (productRequest.getImageUrls() != null) {
            for (String url : productRequest.getImageUrls()) {
                ProductImage img = new ProductImage();
                img.setProductId(id);
                img.setImageUrl(url);
                productImageService.saveProductImage(img);
            }
        }

        return ResponseEntity.ok(ApiResponse.ok(null, "상품이 수정되었습니다."));
    }

    // ✅ 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        adminProductService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "상품이 삭제되었습니다."));
    }

    // ✅ 상품 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable("id") Long id, @RequestParam(name = "status") String status) {
        adminProductService.updateProductStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok(null, "상품 상태가 변경되었습니다."));
    }

    // ✅ 상품 상태별 목록 확인
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<ProductAdminResponseDto>>> getByStatus(@RequestParam(name = "status") String status) {
        List<Product> products = adminProductService.getProductsByStatus(status);

        List<ProductAdminResponseDto> dtos = products.stream()
            .map(p -> ProductDtoConverter.toAdminResponse(
                p,
                productImageService.getImagesByProductId(p.getProductId())
                                   .stream()
                                   .map(ProductImage::getImageUrl)
                                   .toList()
            ))
            .toList();

        return ResponseEntity.ok(ApiResponse.ok(dtos));
    }
}
