package com.itbank.mall.dto.admin.product;

import lombok.Data;

import java.util.List;

import com.itbank.mall.entity.product.Product;

@Data
public class ProductRequestDto{
    private Long productId;
    private String name;
    private int price;
    private int stock;
    private Long categoryId;
    private String description;
    private String status;
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
        return product;
    }
}
