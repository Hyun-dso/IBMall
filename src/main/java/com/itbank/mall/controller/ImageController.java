package com.itbank.mall.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/image")
public class ImageController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, String> response = new HashMap<>();

        if (file.isEmpty() || file.getOriginalFilename() == null) {
            response.put("status", "fail");
            response.put("message", "업로드할 파일이 없습니다.");
            return response;
        }

        // 폴더 없으면 생성
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destFile = new File(uploadDir, fileName);
        file.transferTo(destFile);

        String imageUrl = "/upload/productImg/" + fileName;
        response.put("status", "success");
        response.put("imageUrl", imageUrl);

        return response;
    }
}
