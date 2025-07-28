package com.itbank.mall.controller.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itbank.mall.dto.admin.image.ImageUploadRequestDto;
import com.itbank.mall.entity.product.ProductImage;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.admin.ProductImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/images")
@RequiredArgsConstructor
public class ImageController {

    @Value("${file.upload.path}")
    private String uploadPath;

    private final ProductImageService productImageService;

    @PostMapping("/upload")
    public ApiResponse<List<String>> uploadImages(@ModelAttribute ImageUploadRequestDto dto) {

        Long productId = dto.getProductId();
        List<MultipartFile> files = dto.getFiles();

        if (productId == null) {
            return ApiResponse.fail("상품 ID가 없습니다.");
        }

        if (files == null || files.isEmpty()) {
            return ApiResponse.fail("업로드할 파일이 없습니다.");
        }

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            return ApiResponse.fail("업로드 디렉토리 생성 실패");
        }

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destFile = new File(uploadDir, fileName);

            try {
                file.transferTo(destFile);
                String imageUrl = "/upload/productImg/" + fileName;
                imageUrls.add(imageUrl);

                ProductImage productImage = new ProductImage();
                productImage.setProductId(productId);
                productImage.setImageUrl(imageUrl);
                productImageService.saveProductImage(productImage);

            } catch (IOException e) {
                return ApiResponse.fail("파일 업로드 실패: " + file.getOriginalFilename());
            }
        }

        if (imageUrls.isEmpty()) {
            return ApiResponse.fail("업로드된 파일이 없습니다.");
        }

        return ApiResponse.ok(imageUrls, "이미지 업로드 및 상품 연결 성공");
    }
}	