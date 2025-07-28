package com.itbank.mall.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itbank.mall.entity.grade.GradeBenefit;
import com.itbank.mall.mapper.admin.GradeBenefitMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradeBenefitService {

    private final GradeBenefitMapper gradeBenefitMapper;

    public List<GradeBenefit> getAll() {
        return gradeBenefitMapper.findAll();
    }

    public GradeBenefit getByGrade(String gradeName) {
        return gradeBenefitMapper.findByGrade(gradeName);
    }

    public int update(GradeBenefit benefit) {
        return gradeBenefitMapper.updateBenefit(benefit);
    }
}
