package com.itbank.mall.dto.admin.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 역할 변경 결과 응답
 */
@Getter
@Builder
@AllArgsConstructor
public class RoleChangeResponseDto {
    private final Long memberId;
    private final String previousRole;
    private final String newRole;
    private final boolean needReLogin; // 역할 변경 후 재로그인 필요(블랙리스트 연동 예정)
}
