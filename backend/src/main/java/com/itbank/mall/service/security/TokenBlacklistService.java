package com.itbank.mall.service.security;

public interface TokenBlacklistService {
	
    /** 로그인/재발급 시 활성 토큰 인덱스 등록 */
    void onIssueTokens(Long memberId, String accessJti, long accessExpEpoch, String refreshJti, long refreshExpEpoch);

    /** 현재 토큰(또는 지정한 토큰)만 블랙리스트 처리 */
    void blacklistByJtis(String accessJti, long accessExpEpoch, String refreshJti, long refreshExpEpoch);

    /** 특정 회원의 모든 활성 토큰 일괄 블랙리스트 처리 (역할 변경/강제 로그아웃) */
    void blacklistAllActiveTokensForMember(Long memberId);

    /** 필터에서 빠르게 사용할 AccessToken 블랙리스트 여부 */
    boolean isBlacklistedAccess(String accessJti);
}
