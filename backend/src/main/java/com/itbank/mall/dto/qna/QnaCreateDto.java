package com.itbank.mall.dto.qna;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaCreateDto {

    @JsonProperty("member_id")
    private Long memberId;

    private String title;
    private String content;

    @JsonProperty("is_secret")
    private boolean isSecret;
}
