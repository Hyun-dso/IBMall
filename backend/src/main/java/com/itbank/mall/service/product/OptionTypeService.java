package com.itbank.mall.service.product;

import com.itbank.mall.entity.product.OptionType;
import java.util.List;

public interface OptionTypeService {

    // ✅ 컨트롤러에서 쓰는 메서드
    List<OptionType> findAll();
    int deleteById(int id);

    // ✅ 기존에 있던 메서드도 필요하면 유지
    List<OptionType> getOptionTypesByCategoryId(int categoryId);
    int addOptionType(OptionType optionType);
    // int deleteOptionType(int id); // 쓰지 않으면 삭제하거나 deleteById로 통일
}
