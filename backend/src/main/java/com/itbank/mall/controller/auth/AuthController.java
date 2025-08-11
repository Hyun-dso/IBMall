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
import com.itbank.mall.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final Environment env;

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

            // Access / Refresh 발급
            String accessToken  = jwtUtil.generateToken(member.getId(), member.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(member.getId(), member.getEmail());

            ResponseCookie accessCookie = baseCookie("accessToken", accessToken)
                    .maxAge(Duration.ofMinutes(30))
                    .build();
            ResponseCookie refreshCookie = baseCookie("refresh_token", refreshToken)
                    .maxAge(Duration.ofDays(7))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return ResponseEntity.ok(Map.of("message", "로그인 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout(HttpServletResponse response) {
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
        String newAccessToken = jwtUtil.generateToken(memberId, email);

        ResponseCookie newAccessCookie = baseCookie("accessToken", newAccessToken)
                .maxAge(Duration.ofMinutes(30))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, newAccessCookie.toString());
        return ResponseEntity.ok(Map.of("message", "Access Token 재발급 완료"));
    }
}
