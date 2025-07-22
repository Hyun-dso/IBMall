package com.itbank.mall.dto.admin.image;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ImageUploadRequestDto {
	private Long productId;
    private List<MultipartFile> files;

    // 추후 확장 가능 예시
    // private String imageType;     // ex) THUMBNAIL, DETAIL
    // private Long productId;
}
