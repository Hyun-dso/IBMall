package com.itbank.mall.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;     // 응답용 (생성 시 DB가 채움)
    private String name; // 요청/수정 시 필수
}
