package com.itbank.mall.controller;

import com.itbank.mall.dto.ImageUploadRequestDto;
import com.itbank.mall.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/admin/images")
public class ImageController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<List<String>>> uploadImages(@ModelAttribute ImageUploadRequestDto dto) {
        List<MultipartFile> files = dto.getFiles();

        if (files == null || files.isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail("업로드할 파일이 없습니다."));
        }

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("업로드 디렉토리 생성 실패"));
        }

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destFile = new File(uploadDir, fileName);

            try {
                file.transferTo(destFile);
                imageUrls.add("/upload/productImg/" + fileName);
            } catch (IOException e) {
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("파일 업로드 실패: " + file.getOriginalFilename()));
            }
        }

        if (imageUrls.isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail("업로드된 파일이 없습니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(imageUrls, "이미지 업로드 성공"));
    }
}
