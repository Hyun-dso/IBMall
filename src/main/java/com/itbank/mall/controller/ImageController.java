package com.itbank.mall.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/image")
public class ImageController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> uploadImages(@RequestParam("files") MultipartFile[] files) throws IOException {
        Map<String, Object> response = new HashMap<>();

        System.out.println("✅ [ImageController] 업로드 요청 들어옴");
        System.out.println("받은 파일 개수: " + (files != null ? files.length : 0));

        if (files == null || files.length == 0) {
            response.put("status", "fail");
            response.put("message", "업로드할 파일이 없습니다.");
            System.out.println("❌ 파일 없음, 업로드 종료");
            return response;
        }

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            System.out.println("📂 업로드 폴더 생성됨: " + uploadDir.getAbsolutePath() + " (성공 여부: " + created + ")");
        }

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            System.out.println("👉 처리 중 파일: " + file.getOriginalFilename());
            if (file.isEmpty()) {
                System.out.println("⚠️ 빈 파일, 스킵");
                continue;
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destFile = new File(uploadDir, fileName);
            file.transferTo(destFile);
            System.out.println("✅ 저장 완료: " + destFile.getAbsolutePath());

            String imageUrl = "/upload/productImg/" + fileName;
            imageUrls.add(imageUrl);
        }

        System.out.println("✅ 업로드 완료된 파일 개수: " + imageUrls.size());

        if (imageUrls.isEmpty()) {
            response.put("status", "fail");
            response.put("message", "업로드된 파일이 없습니다.");
        } else {
            response.put("status", "success");
            response.put("imageUrls", imageUrls);
        }

        return response;
    }
}
