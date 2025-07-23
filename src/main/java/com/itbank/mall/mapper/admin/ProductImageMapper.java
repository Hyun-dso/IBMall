package com.itbank.mall.mapper.admin;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.itbank.mall.entity.product.ProductImage;

public interface ProductImageMapper {

    // 이미지 1개 저장
    void insert(ProductImage productImage);

    // 특정 상품의 이미지 전체 조회
    List<ProductImage> findByProductId(Long productId);

    // 특정 상품의 이미지 전체 삭제
    int deleteByProductId(Long productId);

    // 썸네일 설정 (product 테이블의 thumbnail_url 업데이트)
    void updateProductThumbnail(@Param("productId") Long productId, @Param("imageId") Long imageId);

    // 이미지 정렬 순서 변경
    int updateSortOrder(@Param("imageId") Long imageId, @Param("sortOrder") int sortOrder);
}
