package com.itbank.mall.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.itbank.mall.entity.grade.GradeBenefit;

public interface GradeBenefitMapper {

    List<GradeBenefit> findAll();

    GradeBenefit findByGrade(@Param("gradeName") String gradeName);

    int updateBenefit(GradeBenefit benefit);
}
