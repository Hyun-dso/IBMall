// src/main/java/com/itbank/mall/service/security/NoopTokenBlacklistService.java
package com.itbank.mall.service.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")   // ★ 로컬에서만 활성
public class NoopTokenBlacklistService implements TokenBlacklistService {
    @Override public void onIssueTokens(Long m, String aj, long ae, String rj, long re) {}
    @Override public void blacklistByJtis(String aj, long ae, String rj, long re) {}
    @Override public void blacklistAllActiveTokensForMember(Long memberId) {}
    @Override public boolean isBlacklistedAccess(String accessJti) { return false; }
}
