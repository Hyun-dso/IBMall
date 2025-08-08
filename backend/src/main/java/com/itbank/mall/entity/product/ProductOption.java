package com.itbank.mall.entity.product;

import lombok.Data;

@Data
public class ProductOption {
    private Long id;
    private Long productId;
    private int optionName;
    private String optionValue;
    
}
