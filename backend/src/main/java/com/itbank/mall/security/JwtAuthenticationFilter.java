package com.itbank.mall.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.itbank.mall.util.JwtUtil;
// import com.itbank.mall.service.security.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    // private final TokenBlacklistService tokenBlacklistService;

    // 1) 로그인/재발급/가입 등은 필터 스킵
    private static final List<String> SKIP_PATHS = List.of(
        "/api/auth/signin",
        "/api/auth/refresh-token",
        "/api/members/signup",
        "/actuator/health"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true; // CORS preflight
        return SKIP_PATHS.stream().anyMatch(uri::equals);
        // 필요하면 startsWith로 공개 GET 경로 추가 가능
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        try {
            // 2) 쿠키에서 AccessToken 추출 (없으면 통과)
            String token = jwtUtil.resolveToken(request);
            if (token == null || token.isBlank()) {
                chain.doFilter(request, response);
                return;
            }

            // 3) 유효성 체크 실패해도 컨트롤러로 넘김(인증만 세우지 않음)
            if (!jwtUtil.validateToken(token)) {
                chain.doFilter(request, response);
                return;
            }

            // 4) (옵션) 블랙리스트 검사 — 블랙이면 인증 세우지 않고 통과
            // String jti = null;
            // try { jti = jwtUtil.getJti(token); } catch (Exception ignore) {}
            // if (jti != null && tokenBlacklistService.isBlacklistedAccess(jti)) {
            //     chain.doFilter(request, response);
            //     return;
            // }

            // 5) 인증 컨텍스트 세팅
            String email = jwtUtil.getEmailFromToken(token);
            var userDetails = userDetailsService.loadUserByUsername(email);
            var auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            chain.doFilter(request, response);
        } catch (Exception ex) {
            // 6) 절대 여기서 응답 종료하지 말 것 — 로그만 찍고 통과
            log.error("JWT filter error", ex);
            SecurityContextHolder.clearContext();
            chain.doFilter(request, response);
        }
    }
}