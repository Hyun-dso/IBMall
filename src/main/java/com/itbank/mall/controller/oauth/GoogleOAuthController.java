package com.itbank.mall.controller.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.itbank.mall.dto.oauth.TempUserDto;
import com.itbank.mall.service.member.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
public class GoogleOAuthController {

    private final MemberService memberService;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    @Value("${google.client.secret}")
    private String clientSecret;

    @GetMapping("/authorize/google")
    public ResponseEntity<?> getGoogleLoginUrl() {
        String url = UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid profile email")
                .queryParam("access_type", "offline")
                .build().toUriString();

        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping("/callback/google")
    public ResponseEntity<?> googleCallback(@RequestParam("code") String code) {
        try {
            // 1. Access Token 요청
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("grant_type", "authorization_code");

            WebClient webClient = WebClient.builder()
                    .baseUrl("https://oauth2.googleapis.com")
                    .defaultHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            Map<String, Object> response = webClient.post()
                    .uri("/token")
                    .bodyValue(params)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            log.info("🪙 토큰 응답: {}", response);
            if (response == null || !response.containsKey("access_token")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body(Map.of("message", "토큰 발급 실패"));
            }

            String accessToken = (String) response.get("access_token");

            // 2. 사용자 정보 요청
            Map<String, Object> userInfo = webClient.get()
                    .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            log.info("👤 구글 사용자 정보: {}", userInfo);
            if (userInfo == null || !userInfo.containsKey("email")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body(Map.of("message", "사용자 정보 수신 실패"));
            }

            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");
            String providerId = (String) userInfo.get("id");

            boolean exists = memberService.existsByEmail(email);

            if (!exists) {
                return ResponseEntity.ok(Map.of(
                    "signupRequired", true,
                    "tempUser", new TempUserDto(email, name, providerId)
                ));
            }

            return ResponseEntity.ok(Map.of(
                "signupRequired", false,
                "message", "기존 회원입니다. 로그인 처리 진행 가능"
            ));

        } catch (Exception e) {
            log.error("❌ Google OAuth 처리 중 예외", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "Google OAuth 처리 중 오류가 발생했습니다."));
        }
    }
}
