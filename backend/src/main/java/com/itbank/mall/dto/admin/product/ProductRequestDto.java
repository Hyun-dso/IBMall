package com.itbank.mall.dto.admin.product;

import com.itbank.mall.entity.product.Product;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductRequestDto {
    private Long productId;
    private String name;
    private int price;
    private int stock;
    private Long categoryId;
    private String description;
    private String status;

    private Boolean isTimeSale;               // ✅ 추가
    private Integer timeSalePrice;            // ✅ 추가
    private LocalDateTime timeSaleStart;      // ✅ 추가
    private LocalDateTime timeSaleEnd;        // ✅ 추가

    private List<String> imageUrls;

    public Product toProduct() {
        Product product = new Product();
        product.setProductId(productId);
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategoryId(categoryId);
        product.setDescription(description);
        product.setStatus(status);
        product.setIsTimeSale(isTimeSale);
        product.setTimeSalePrice(timeSalePrice);
        product.setTimeSaleStart(timeSaleStart);
        product.setTimeSaleEnd(timeSaleEnd);
        return product;
    }
}
