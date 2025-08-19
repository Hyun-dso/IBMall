package com.itbank.mall.dto.qna;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QnaDto {

    private Long qnaId;

    private Long memberId;

    private String title;
    private String content;

    @JsonProperty("is_secret")
    private boolean isSecret;

    private String answer;

    @JsonProperty("answered_at")
    private LocalDateTime answeredAt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    private String nickname;
}
