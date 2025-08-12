package com.itbank.mall.entity.product;

import lombok.Data;

@Data
public class ProductOption {
    private long id;
    private long productId;
    private int optionTypeId; //rename & type fix  
    private String optionValue;
    private int extraPrice;
    private int stock;
} 