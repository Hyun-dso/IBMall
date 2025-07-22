package com.itbank.mall.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.itbank.mall.entity.grade.GradeRuleEntity;

@Mapper
public interface GradeRuleMapper {
	List<GradeRuleEntity> selectAllRules();
	int updateRule(GradeRuleEntity rule);

}
