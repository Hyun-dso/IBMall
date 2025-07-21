package com.itbank.mall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 폴더 없으면 생성
        File folder = new File(uploadPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 리소스 핸들러 등록
        registry.addResourceHandler("/upload/productImg/**")
                .addResourceLocations("file:///" + uploadPath.replace("\\", "/"));
    }
}

