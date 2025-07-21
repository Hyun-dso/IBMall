package com.itbank.mall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductDetailResponseDto {
    private ProductUserResponseDto product;
    private List<String> images;
}
