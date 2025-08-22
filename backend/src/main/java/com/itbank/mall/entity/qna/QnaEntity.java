package com.itbank.mall.entity.qna;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QnaEntity {

    private Long qnaId;
    private Long memberId;

    private String title;
    private String content;

    private boolean isSecret;

    private String answer;
    private LocalDateTime answeredAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
