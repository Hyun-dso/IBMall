package com.itbank.mall.dto.admin.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 변경할 역할을 전달한다.
 * 허용 값: USER | SELLER  (ADMIN로의 승격은 별도 정책/엔드포인트로 운용 권장)
 */
@Getter @Setter
public class RoleUpdateRequestDto {

    @NotBlank(message = "role은 필수입니다 (USER|SELLER)")
    private String role;
}
