package com.itbank.mall.dto.user.product;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductDetailResponseDto {
    private ProductUserResponseDto product;
    private List<String> images;
}
