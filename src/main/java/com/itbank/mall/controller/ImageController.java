package com.itbank.mall.controller;

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

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, String> response = new HashMap<>();

        if (file.isEmpty() || file.getOriginalFilename() == null) {
            response.put("status", "fail");
            response.put("message", "업로드할 파일이 없습니다.");
            return response;
        }

        // ✅ 수정: 외부 경로로 변경
        String uploadDir = "C:/ibmall-upload/";
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destFile = new File(uploadPath, fileName);
        file.transferTo(destFile);

        String imageUrl = "/upload/" + fileName;
        response.put("status", "success");
        response.put("imageUrl", imageUrl);

        return response;
    }
}

