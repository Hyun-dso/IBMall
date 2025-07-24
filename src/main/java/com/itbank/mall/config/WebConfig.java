package com.itbank.mall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String productUploadPath;

    @Value("${file.messageimg.path}")
    private String messageImgUploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 상품 이미지 디렉토리 생성
        File productFolder = new File(productUploadPath);
        if (!productFolder.exists()) {
            productFolder.mkdirs();
        }

        // 쪽지 이미지 디렉토리 생성
        File messageFolder = new File(messageImgUploadPath);
        if (!messageFolder.exists()) {
            messageFolder.mkdirs();
        }

        // 상품 이미지 접근 경로 등록
        registry.addResourceHandler("/upload/productImg/**")
                .addResourceLocations("file:///" + productUploadPath.replace("\\", "/"));

        // 쪽지 이미지 접근 경로 등록
        registry.addResourceHandler("/upload/messageImg/**")
                .addResourceLocations("file:///" + messageImgUploadPath.replace("\\", "/"));
    }
}

