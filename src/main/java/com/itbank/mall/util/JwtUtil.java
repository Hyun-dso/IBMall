package com.itbank.mall.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationSeconds;  // 초 단위

    @PostConstruct
    private void validateSecretKey() {
        if (secretKey.length() < 32) {
            throw new IllegalArgumentException("🔒 JWT 시크릿 키는 최소 32바이트 이상이어야 합니다.");
        }
    }

    public String generateToken(Long memberId, String email) {
        long now = System.currentTimeMillis();
        long expirationTime = now + expirationSeconds * 1000L;

        return Jwts.builder()
                .setSubject(email)
                .claim("memberId", memberId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expirationTime))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    
    public Long getMemberId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("memberId", Long.class);
    }
}
