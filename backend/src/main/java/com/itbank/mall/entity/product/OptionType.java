package com.itbank.mall.entity.product;

import lombok.Data;

@Data
public class OptionType {
    private int id;             // PK
    private String name;        // ex: 색상, 사이즈 등
    private int categoryId;     // 해당 옵션이 속한 카테고리 ID (ex: 신발, 의류 등)
}
