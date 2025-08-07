package com.itbank.mall.util;

import java.util.List;

import com.itbank.mall.dto.admin.product.ProductAdminResponseDto;
import com.itbank.mall.dto.user.product.ProductUserResponseDto;
import com.itbank.mall.entity.product.Product;

public class ProductDtoConverter {

	public static ProductAdminResponseDto toAdminResponse(Product product, List<String> imageUrls) {
	    ProductAdminResponseDto dto = new ProductAdminResponseDto();
	    dto.setProductId(product.getProductId());
	    dto.setName(product.getName());
	    dto.setPrice(product.getPrice());
	    dto.setStock(product.getStock());
	    dto.setCategoryId(product.getCategoryId());
	    dto.setDescription(product.getDescription());
	    dto.setThumbnailUrl(product.getThumbnailUrl());
	    dto.setViewCount(product.getViewCount());
	    dto.setRecommendCount(product.getRecommendCount());
	    dto.setNotRecommendCount(product.getNotRecommendCount());
	    dto.setIsTimeSale(product.getIsTimeSale());
	    dto.setTimeSalePrice(product.getTimeSalePrice());
	    dto.setCreatedAt(product.getCreatedAt());
	    dto.setStatus(product.getStatus());
	    dto.setImageUrls(imageUrls);
	    return dto;
	}
	
    public static ProductUserResponseDto toUserResponse(Product p) {
        ProductUserResponseDto dto = new ProductUserResponseDto();
        dto.setProductId(p.getProductId());
        dto.setName(p.getName());
        dto.setPrice(p.getPrice());
        dto.setThumbnailUrl(p.getThumbnailUrl());
        dto.setDescription(p.getDescription());
        dto.setIsTimeSale(p.getIsTimeSale());
        dto.setTimeSalePrice(p.getTimeSalePrice());
        return dto;
    }
}
