package com.itbank.mall.mapper;

import java.util.List;

import com.itbank.mall.entity.GradeRuleEntity;

public interface GradeRuleMapper {
	List<GradeRuleEntity> selectAllRules();
	int updateRule(GradeRuleEntity rule);

}
