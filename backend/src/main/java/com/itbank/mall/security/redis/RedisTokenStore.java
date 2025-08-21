package com.itbank.mall.security.redis;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisTokenStore {

    private final StringRedisTemplate redis;

    private static String kBLAccess(String jti) { return "bl:access:" + jti; }
    private static String kBLRefresh(String jti) { return "bl:refresh:" + jti; }
    private static String kAT(Long memberId)     { return "at:" + memberId; }
    private static String kRT(Long memberId)     { return "rt:" + memberId; }

    public void registerAccess(Long memberId, String jti, long expEpochSec) {
        redis.opsForZSet().add(kAT(memberId), jti, expEpochSec);
    }

    public void registerRefresh(Long memberId, String jti, long expEpochSec) {
        redis.opsForZSet().add(kRT(memberId), jti, expEpochSec);
    }

    public void blacklistAccess(String jti, long ttlSeconds) {
        if (ttlSeconds > 0) redis.opsForValue().set(kBLAccess(jti), "1", java.time.Duration.ofSeconds(ttlSeconds));
    }

    public void blacklistRefresh(String jti, long ttlSeconds) {
        if (ttlSeconds > 0) redis.opsForValue().set(kBLRefresh(jti), "1", java.time.Duration.ofSeconds(ttlSeconds));
    }

    public boolean isAccessBlacklisted(String jti) {
        Boolean exists = redis.hasKey(kBLAccess(jti));
        return exists != null && exists;
    }

    public List<String> findActiveAccessJtis(Long memberId) {
        long now = Instant.now().getEpochSecond();
        Set<String> s = redis.opsForZSet().rangeByScore(kAT(memberId), now + 1, Double.POSITIVE_INFINITY);
        return s == null ? List.of() : new ArrayList<>(s);
    }

    public List<String> findActiveRefreshJtis(Long memberId) {
        long now = Instant.now().getEpochSecond();
        Set<String> s = redis.opsForZSet().rangeByScore(kRT(memberId), now + 1, Double.POSITIVE_INFINITY);
        return s == null ? List.of() : new ArrayList<>(s);
    }

    public Long getExpFromIndex(Long memberId, String jti, boolean access) {
        Double score = redis.opsForZSet().score(access ? kAT(memberId) : kRT(memberId), jti);
        return score == null ? null : score.longValue();
    }

    /** 만료 지난 인덱스 청소 (선택적) */
    public void cleanupIndexes(Long memberId) {
        long now = Instant.now().getEpochSecond();
        redis.opsForZSet().removeRangeByScore(kAT(memberId), 0, now);
        redis.opsForZSet().removeRangeByScore(kRT(memberId), 0, now);
    }

    /** 여러 jti 블랙리스트를 파이프라인으로 처리 */
    public void pipelineBlacklist(List<BLItem> items) {
        if (items == null || items.isEmpty()) return;
        redis.executePipelined(new SessionCallback<Object>() {
            @Override public <K, V> Object execute(org.springframework.data.redis.core.RedisOperations<K, V> ops) throws DataAccessException {
                for (BLItem it : items) {
                    long ttl = it.ttlSeconds;
                    if (ttl <= 0) continue;
                    if (it.type == BLType.ACCESS) {
                        ((StringRedisTemplate)ops).opsForValue().set(kBLAccess(it.jti), "1", java.time.Duration.ofSeconds(ttl));
                    } else {
                        ((StringRedisTemplate)ops).opsForValue().set(kBLRefresh(it.jti), "1", java.time.Duration.ofSeconds(ttl));
                    }
                }
                return null;
            }
        });
    }

    public enum BLType { ACCESS, REFRESH }
    public static record BLItem(BLType type, String jti, long ttlSeconds) {}
}
