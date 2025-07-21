package com.itbank.mall.controller;

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
    public ResponseEntity<Map<String, Object>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        Map<String, Object> response = new HashMap<>();

        if (files == null || files.length == 0) {
            response.put("code", "FAIL_NO_FILES");
            response.put("message", "업로드할 파일이 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (!created) {
                response.put("code", "FAIL_DIR_CREATION");
                response.put("message", "업로드 디렉토리 생성 실패.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destFile = new File(uploadDir, fileName);

            try {
                file.transferTo(destFile);
                String imageUrl = "/upload/productImg/" + fileName;
                imageUrls.add(imageUrl);
            } catch (IOException e) {
                response.put("code", "FAIL_UPLOAD");
                response.put("message", "파일 업로드 실패: " + file.getOriginalFilename());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }

        if (imageUrls.isEmpty()) {
            response.put("code", "FAIL_EMPTY_FILES");
            response.put("message", "업로드된 파일이 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("code", "SUCCESS");
        response.put("imageUrls", imageUrls);
        return ResponseEntity.ok(response);
    }
}
