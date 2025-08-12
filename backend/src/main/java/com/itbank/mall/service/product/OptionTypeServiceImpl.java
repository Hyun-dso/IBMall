package com.itbank.mall.service.product;

import com.itbank.mall.entity.product.OptionType;
import com.itbank.mall.mapper.product.OptionTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionTypeServiceImpl implements OptionTypeService {

    private final OptionTypeMapper optionTypeMapper;

    @Override
    public List<OptionType> findAll() {
        return optionTypeMapper.findAll();
    }

    @Override
    public List<OptionType> getOptionTypesByCategoryId(int categoryId) {
        return optionTypeMapper.findByCategoryId(categoryId);
    }

    @Override
    public int addOptionType(OptionType optionType) {
        return optionTypeMapper.insert(optionType);
    }

    @Override
    public int deleteById(int id) {
        return optionTypeMapper.deleteById(id);
    }
}
