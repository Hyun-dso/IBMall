package com.itbank.mall.controller.admin;

import com.itbank.mall.dto.admin.role.RoleChangeResponseDto;
import com.itbank.mall.dto.admin.role.RoleUpdateRequestDto;
import com.itbank.mall.response.ApiResponse;
import com.itbank.mall.service.admin.AdminRoleService;
import com.itbank.mall.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
public class AdminRoleController {

    private final AdminRoleService adminRoleService;
    private final JwtUtil jwtUtil;

    /**
     * ADMIN 전용 - 특정 회원의 역할을 USER/SELLER로 변경(부여/회수)
     * 예) USER -> SELLER (부여), SELLER -> USER (회수)
     */
    @PutMapping("/{memberId}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<RoleChangeResponseDto>> changeRole(
            @PathVariable("memberId") Long memberId,
            @Valid @RequestBody RoleUpdateRequestDto request,
            HttpServletRequest httpRequest
    ) {
        // 요청자(관리자) 식별 (HttpOnly 쿠키 기반)
        String token = jwtUtil.resolveToken(httpRequest);
        Long adminId = jwtUtil.getMemberId(token);

        RoleChangeResponseDto result = adminRoleService.changeRole(memberId, request.getRole(), adminId);
        return ResponseEntity.ok(ApiResponse.ok(result, "역할 변경 완료"));
    }
}
