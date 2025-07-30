package com.itbank.mall.dto.qna;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaAnswerDto {

    @JsonProperty("qna_id")
    private Long qnaId;

    private String answer;
}
