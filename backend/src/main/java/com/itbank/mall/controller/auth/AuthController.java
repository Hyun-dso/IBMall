package com.itbank.mall.controller.auth;

import java.time.Duration;
import java.util.Map;

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

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninRequestDto dto, HttpServletResponse response) {
        try {
            Member member = memberService.signin(dto.getEmail(), dto.getPassword());

            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(Map.of("message", "이메일 또는 비밀번호가 틀렸습니다"));
            }

            // ✅ access_token 생성
            String accessToken = jwtUtil.generateToken(member.getId(), member.getEmail());

            ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                    .httpOnly(true)
                    .secure(false)  // 운영환경에서는 true
                    .path("/")
                    .maxAge(Duration.ofMinutes(30))  // access_token은 짧게 (예: 30분)
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

            // ✅ refresh_token 생성
            String refreshToken = jwtUtil.generateRefreshToken(member.getId(), member.getEmail());

            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofDays(7))  // refresh_token은 길게
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return ResponseEntity.ok(Map.of("message", "로그인 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }


    @PostMapping("/signout")
    public ResponseEntity<?> signout(HttpServletResponse response) {
        ResponseCookie expiredCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)  // 운영 환경에서는 true
                .path("/")
                .maxAge(0)      // ★ 쿠키 즉시 만료
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

        return ResponseEntity.ok(Map.of("message", "로그아웃 완료"));
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;

        // ✅ 쿠키에서 refresh_token 추출
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

        // ✅ refresh_token이 유효하면 새 access_token 발급
        String email = jwtUtil.getEmailFromToken(refreshToken);
        Long memberId = jwtUtil.getMemberId(refreshToken);
        String newAccessToken = jwtUtil.generateToken(memberId, email);

        ResponseCookie newAccessCookie = ResponseCookie.from("access_token", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMinutes(30))  // access_token은 짧게
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, newAccessCookie.toString());

        return ResponseEntity.ok(Map.of("message", "Access Token 재발급 완료"));
    }
}
