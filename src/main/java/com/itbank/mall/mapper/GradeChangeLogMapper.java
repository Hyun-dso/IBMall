package com.itbank.mall.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.itbank.mall.dto.GradeChangeLogDto;

@Mapper
public interface GradeChangeLogMapper {

    int insertGradeChangeLog(GradeChangeLogDto dto);

    List<GradeChangeLogDto> selectGradeChangeLogs();
}