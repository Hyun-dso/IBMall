package com.itbank.mall.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.itbank.mall.dto.admin.grade.GradeChangeLogDto;

@Mapper
public interface GradeChangeLogMapper {

    int insertGradeChangeLog(GradeChangeLogDto dto);

    List<GradeChangeLogDto> selectGradeChangeLogs();
}