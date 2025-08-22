package com.itbank.mall.controller.auth;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbank.mall.dto.auth.SigninRequestDto;
import com.itbank.mall.entity.member.Member;
import com.itbank.mall.service.member.MemberService;
import com.itbank.mall.service.security.TokenBlacklistService;
import com.itbank.mall.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final Environment env;
    private final TokenBlacklistService tokenBlacklistService;

    private boolean isProd() {
        return Arrays.stream(env.getActiveProfiles())
                     .anyMatch(p -> p.equalsIgnoreCase("prod") || p.equalsIgnoreCase("production"));
    }

    private ResponseCookie.ResponseCookieBuilder baseCookie(String name, String value) {
        boolean prod = isProd();
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(name, value)
                .httpOnly(true)
                .path("/");

        if (prod) {
            b.secure(true)
             .sameSite("None")
             .domain(".ibmall.shop");   // 서브도메인 포함
        } else {
            b.secure(false)
             .sameSite("Lax");
        }
        return b;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninRequestDto dto, HttpServletResponse response) {
        try {
            Member member = memberService.signin(dto.getEmail(), dto.getPassword());
            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "이메일 또는 비밀번호가 틀렸습니다"));
            }
            log.debug("signin OK memberId={}", member.getId());

            // 토큰 발급
            String accessToken  = jwtUtil.generateToken(member.getId(), member.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(member.getId(), member.getEmail());
            if (accessToken == null || refreshToken == null) {
                log.error("token issue failed: at={}, rt={}", accessToken, refreshToken);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "토큰 발급 실패"));
            }

            // jti/exp 추출 (★ 여기서도 NPE 방지)
            String accessJti  = jwtUtil.getJti(accessToken);
            Long   accessExp  = jwtUtil.getExpiration(accessToken);    // epoch seconds 기대
            String refreshJti = jwtUtil.getJti(refreshToken);
            Long   refreshExp = jwtUtil.getExpiration(refreshToken);
            log.debug("tokens jtiA={}, expA={}, jtiR={}, expR={}", accessJti, accessExp, refreshJti, refreshExp);

            // 블랙리스트 인덱스 등록 (로컬은 Noop)
            try {
                tokenBlacklistService.onIssueTokens(
                    member.getId(),
                    accessJti,  accessExp  != null ? accessExp  : 0L,
                    refreshJti, refreshExp != null ? refreshExp : 0L
                );
                log.debug("blacklist indexed");
            } catch (Exception bex) {
                // 여기서 실패해도 로그인 자체는 성공으로 처리(정책에 따라 조정)
                log.error("blacklist index error", bex);
            }

            // 쿠키
            ResponseCookie accessCookie = baseCookie("accessToken", accessToken)
                    .maxAge(Duration.ofMinutes(30)).build();
            ResponseCookie refreshCookie = baseCookie("refresh_token", refreshToken)
                    .maxAge(Duration.ofDays(7)).build();
            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return ResponseEntity.ok(Map.of("message", "로그인 성공"));
        } catch (Exception e) {
            log.error("signin unhandled error", e);  // ★ 스택 찍기
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }
    
    @PostMapping("/signout")
    public ResponseEntity<?> signout(HttpServletRequest request, HttpServletResponse response) {
        // ★ 쿠키에서 토큰 추출
        String access  = jwtUtil.resolveToken(request);
        String refresh = jwtUtil.resolveRefreshToken(request);

        // ★ jti/exp 추출 후 블랙리스트 처리
        String accessJti  = access  != null ? jwtUtil.getJti(access)        : null;
        Long   accessExp  = access  != null ? jwtUtil.getExpiration(access) : null;
        String refreshJti = refresh != null ? jwtUtil.getJti(refresh)       : null;
        Long   refreshExp = refresh != null ? jwtUtil.getExpiration(refresh): null;

        tokenBlacklistService.blacklistByJtis(
            accessJti,  accessExp  != null ? accessExp  : 0L,
            refreshJti, refreshExp != null ? refreshExp : 0L
        );

        // 기존 쿠키 삭제
        ResponseCookie expiredAccess = baseCookie("accessToken", "")
                .maxAge(0).build();
        ResponseCookie expiredRefresh = baseCookie("refresh_token", "")
                .maxAge(0).build();

        response.addHeader(HttpHeaders.SET_COOKIE, expiredAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiredRefresh.toString());

        return ResponseEntity.ok(Map.of("message", "로그아웃 완료"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh Token이 유효하지 않음"));
        }

        Long memberId = jwtUtil.getMemberId(refreshToken);
        String email  = jwtUtil.getEmailFromToken(refreshToken);

        // 새 Access 발급
        String newAccessToken = jwtUtil.generateToken(memberId, email);

        // ★ 새 Access 인덱스 등록 (refresh는 재사용이므로 null로 전달)
        String accessJti = jwtUtil.getJti(newAccessToken);
        long   accessExp = jwtUtil.getExpiration(newAccessToken);
        tokenBlacklistService.onIssueTokens(memberId, accessJti, accessExp, null, 0L); // ★

        ResponseCookie newAccessCookie = baseCookie("accessToken", newAccessToken)
                .maxAge(Duration.ofMinutes(30))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, newAccessCookie.toString());
        return ResponseEntity.ok(Map.of("message", "Access Token 재발급 완료"));
    }
}
