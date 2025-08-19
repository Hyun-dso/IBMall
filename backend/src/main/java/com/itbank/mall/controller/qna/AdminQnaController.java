package com.itbank.mall.controller.qna;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.qna.QnaAnswerDto;
import com.itbank.mall.dto.qna.QnaDto;
import com.itbank.mall.service.qna.QnaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/qna")
@RequiredArgsConstructor
public class AdminQnaController {

    private final QnaService qnaService;

    // ✅ 전체 QnA 목록 조회
    @GetMapping
    public List<QnaDto> getAllQnaList() {
        return qnaService.getAllQnaList();
    }
    // ✅ 2. QnA 상세 조회 (선택)
    @GetMapping("/{qnaId}")
    public QnaDto getQnaDetail(@PathVariable("qnaId") Long qnaId) {
        return qnaService.getQnaDetail(qnaId);
    }

    // ✅ 답변 등록
    @PutMapping("/answer")
    public ResponseEntity<String> answerQna(@RequestBody QnaAnswerDto dto) {
        int result = qnaService.answerQna(dto);
        return result > 0
                ? ResponseEntity.ok("답변 등록 완료")
                : ResponseEntity.badRequest().body("답변 등록 실패");
    }
}
