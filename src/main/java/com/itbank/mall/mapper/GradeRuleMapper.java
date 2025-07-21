package com.itbank.mall.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.itbank.mall.entity.GradeRuleEntity;

@Mapper
public interface GradeRuleMapper {
	List<GradeRuleEntity> selectAllRules();
	int updateRule(GradeRuleEntity rule);

}
