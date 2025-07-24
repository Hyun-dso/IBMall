package com.itbank.mall.controller;

import com.itbank.mall.service.MessageImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/message")
@RequiredArgsConstructor
public class MessageImageController {

    private final MessageImageService messageImageService;

    @PostMapping("/upload_image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = messageImageService.save(file);
            return ResponseEntity.ok(imageUrl);  // 프론트는 이걸 imageUrl로 DTO에 넣으면 됨
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이미지 업로드 실패: " + e.getMessage());
        }
    }
}
