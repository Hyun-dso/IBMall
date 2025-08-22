package com.itbank.mall.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")           // AccessToken 유효기간(초)
    private long expirationSeconds;       // e.g. 1800 (30분)

    @Value("${jwt.refresh-expiration:604800}") // RefreshToken 유효기간(초) [기본: 7일]
    private long refreshExpirationSeconds;

    private byte[] key() {
        return secretKey.getBytes(StandardCharsets.UTF_8);
    }

    @PostConstruct
    private void validateSecretKey() {
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalArgumentException("🔒 JWT 시크릿 키는 최소 32바이트 이상이어야 합니다.");
        }
    }

    /** AccessToken 발급: jti 포함 */
    public String generateToken(Long memberId, String email) {
        long now = System.currentTimeMillis();
        long expirationTime = now + expirationSeconds * 1000L;
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setId(jti) // ★ jti
                .setSubject(email)
                .claim("memberId", memberId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expirationTime))
                .signWith(Keys.hmacShaKeyFor(key()), SignatureAlgorithm.HS256)
                .compact();
    }

    /** RefreshToken 발급: jti 포함 */
    public String generateRefreshToken(Long memberId, String email) {
        long now = System.currentTimeMillis();
        long expirationTime = now + (refreshExpirationSeconds * 1000L);
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setId(jti) // ★ jti
                .setSubject(email)
                .claim("memberId", memberId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expirationTime))
                .signWith(Keys.hmacShaKeyFor(key()), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(key()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public Long getMemberId(String token) {
        Claims c = parseClaims(token);
        Number n = c.get("memberId", Number.class);
        return n != null ? n.longValue() : null;
    }

    /** ★ jti 추출 */
    public String getJti(String token) {
        return parseClaims(token).getId();
    }

    /** ★ 만료 시각(epoch seconds) 추출 */
    public long getExpiration(String token) {
        Date exp = parseClaims(token).getExpiration();
        return exp.getTime() / 1000L;
    }

    /** AccessToken 쿠키 추출 */
    public String resolveToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /** (선택) RefreshToken 쿠키 추출 */
    public String resolveRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("refresh_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /** 내부 공용 */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(key()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
