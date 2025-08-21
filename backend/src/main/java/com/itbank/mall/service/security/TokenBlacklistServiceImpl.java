package com.itbank.mall.service.security;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.itbank.mall.security.redis.RedisTokenStore;

import lombok.RequiredArgsConstructor;

@Service
@Profile({"prod","staging"})
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final RedisTokenStore store;

    @Override
    public void blacklistAllActiveTokensForMember(Long memberId) {
        long now = Instant.now().getEpochSecond();

        var accessJtis = store.findActiveAccessJtis(memberId);
        var refreshJtis = store.findActiveRefreshJtis(memberId);

        List<RedisTokenStore.BLItem> items = new ArrayList<>();

        for (String jti : accessJtis) {
            Long exp = store.getExpFromIndex(memberId, jti, true);
            if (exp == null) continue;
            long ttl = Math.max(1, exp - now);
            items.add(new RedisTokenStore.BLItem(RedisTokenStore.BLType.ACCESS, jti, ttl));
        }
        for (String jti : refreshJtis) {
            Long exp = store.getExpFromIndex(memberId, jti, false);
            if (exp == null) continue;
            long ttl = Math.max(1, exp - now);
            items.add(new RedisTokenStore.BLItem(RedisTokenStore.BLType.REFRESH, jti, ttl));
        }

        store.pipelineBlacklist(items);
        store.cleanupIndexes(memberId); // 선택: 만료분 정리
    }

    /** 로그인/재발급 시 활성 인덱스 등록용 */
    @Override
    public void onIssueTokens(Long memberId, String accessJti, long accessExpEpoch, String refreshJti, long refreshExpEpoch) {
        if (accessJti != null) store.registerAccess(memberId, accessJti, accessExpEpoch);
        if (refreshJti != null) store.registerRefresh(memberId, refreshJti, refreshExpEpoch);
    }

    /** 로그아웃(현재 토큰만) 차단용 */
    @Override
    public void blacklistByJtis(String accessJti, long accessExpEpoch, String refreshJti, long refreshExpEpoch) {
        long now = Instant.now().getEpochSecond();
        if (accessJti != null) store.blacklistAccess(accessJti, Math.max(1, accessExpEpoch - now));
        if (refreshJti != null) store.blacklistRefresh(refreshJti, Math.max(1, refreshExpEpoch - now));
    }

    /** 필터에서 빠르게 사용 */
    @Override
    public boolean isBlacklistedAccess(String accessJti) {
        return accessJti != null && store.isAccessBlacklisted(accessJti);
    }
}
