package com.itbank.mall.mapper.qna;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.itbank.mall.dto.qna.QnaAnswerDto;
import com.itbank.mall.dto.qna.QnaCreateDto;
import com.itbank.mall.dto.qna.QnaDto;

public interface QnaMapper {

    // 사용자
    int insert(QnaCreateDto dto);
    List<QnaDto> selectByMemberId(@Param("memberId") Long memberId);
    QnaDto selectById(@Param("qnaId") Long qnaId);

    // 관리자
    List<QnaDto> selectAll();
    int updateAnswer(QnaAnswerDto dto);
    QnaDto selectDetailById(@Param("qnaId") Long qnaId);
}
