package com.itbank.mall.service.admin;

import com.itbank.mall.dto.admin.role.RoleChangeResponseDto;
import com.itbank.mall.mapper.admin.AdminMemberRoleMapper;
import com.itbank.mall.service.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminRoleService {

    private final AdminMemberRoleMapper roleMapper;
    private final TokenBlacklistService tokenBlacklistService; // 현재 NO-OP, 추후 Redis 연동

    @Transactional
    public RoleChangeResponseDto changeRole(Long targetMemberId, String newRole, Long adminId) {
        // 1) 입력 검증 (ADMIN 전용 API이므로 newRole은 USER | SELLER 만 허용)
        if (newRole == null) throw new IllegalArgumentException("role은 필수입니다");
        newRole = newRole.trim().toUpperCase();
        if (!newRole.equals("USER") && !newRole.equals("SELLER")) {
            throw new IllegalArgumentException("허용되지 않는 역할입니다 (USER|SELLER 만 가능)");
        }

        // 2) 대상 회원 존재 확인
        Boolean exists = roleMapper.existsById(targetMemberId);
        if (exists == null || !exists) {
            throw new IllegalArgumentException("대상 회원을 찾을 수 없습니다");
        }

        // 3) 현재 역할 조회
        String prevRole = roleMapper.findRoleById(targetMemberId);
        if (prevRole == null) prevRole = "USER";
        prevRole = prevRole.toUpperCase();

        // 4) 자기 자신 ADMIN 강등 금지
        if (targetMemberId.equals(adminId) && !newRole.equals("ADMIN") && prevRole.equals("ADMIN")) {
            throw new IllegalStateException("자기 자신을 강등할 수 없습니다");
        }

        // 5) 최후의 ADMIN 보호
        if (prevRole.equals("ADMIN") && !newRole.equals("ADMIN")) {
            int adminCount = roleMapper.countAdmins();
            if (adminCount <= 1) {
                throw new IllegalStateException("최후의 ADMIN은 강등할 수 없습니다");
            }
        }

        // 6) 멱등 처리 (변경 없음)
        if (prevRole.equals(newRole)) {
            return RoleChangeResponseDto.builder()
                    .memberId(targetMemberId)
                    .previousRole(prevRole)
                    .newRole(newRole)
                    .needReLogin(false)
                    .build();
        }

        // 7) 변경 수행
        roleMapper.updateRole(targetMemberId, newRole);

        // 8) 토큰 블랙리스트 (후속 단계: 실제 Redis 연동으로 교체)
//        tokenBlacklistService.blacklistAllActiveTokensForMember(targetMemberId);

        return RoleChangeResponseDto.builder()
                .memberId(targetMemberId)
                .previousRole(prevRole)
                .newRole(newRole)
                .needReLogin(true)
                .build();
    }
}
