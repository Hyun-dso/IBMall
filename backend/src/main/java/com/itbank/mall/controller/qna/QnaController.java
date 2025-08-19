package com.itbank.mall.controller.qna;

import com.itbank.mall.dto.qna.QnaCreateDto;
import com.itbank.mall.dto.qna.QnaDto;
import com.itbank.mall.service.qna.QnaService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    // ✅ 질문 등록
    @PostMapping
    public ResponseEntity<String> create(@RequestBody QnaCreateDto dto) {
        int result = qnaService.createQna(dto);
        return result > 0
                ? ResponseEntity.ok("질문 등록 성공")
                : ResponseEntity.badRequest().body("질문 등록 실패");
    }

    // ✅ 본인 질문 목록
    @GetMapping("/member/{memberId}")
    public List<QnaDto> getUserQnaList(@PathVariable("memberId") Long memberId) {
        return qnaService.getUserQnaList(memberId);
    }

    // ✅ 상세 조회
    @GetMapping("/{qnaId}")
    public QnaDto getQnaDetail(@PathVariable("qnaId") Long qnaId) {
        return qnaService.getQnaDetail(qnaId);
    }
}
