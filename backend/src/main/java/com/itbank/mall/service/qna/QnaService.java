package com.itbank.mall.service.qna;

import com.itbank.mall.dto.qna.QnaAnswerDto;
import com.itbank.mall.dto.qna.QnaCreateDto;
import com.itbank.mall.dto.qna.QnaDto;
import com.itbank.mall.mapper.qna.QnaMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaMapper qnaMapper;

    // 사용자
    public int createQna(QnaCreateDto dto) {
        return qnaMapper.insert(dto);
    }

    public List<QnaDto> getUserQnaList(Long memberId) {
        return qnaMapper.selectByMemberId(memberId);
    }

    public QnaDto getQnaDetail(Long qnaId) {
        return qnaMapper.selectById(qnaId);
    }

    // 관리자
    public List<QnaDto> getAllQnaList() {
        return qnaMapper.selectAll();
    }

    public int answerQna(QnaAnswerDto dto) {
        return qnaMapper.updateAnswer(dto);
    }
    
}
