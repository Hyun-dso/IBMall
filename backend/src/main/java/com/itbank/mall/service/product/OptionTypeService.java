package com.itbank.mall.service.product;

import com.itbank.mall.entity.product.OptionType;

import java.util.List;

public interface OptionTypeService {
    List<OptionType> getOptionTypesByCategoryId(int categoryId);
    int addOptionType(OptionType optionType);
    int deleteOptionType(int id);
}
