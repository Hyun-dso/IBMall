package com.itbank.mall.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.itbank.mall.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/js/**", "/css/**", "/images/**",
                    "/api/auth/**", "/api/members/signup",
                    "/api/members/check-nickname", "/api/members/check-email", "/api/members/check-phone",
                    "/api/email/**", "/api/password/**", "/api/oauth2/**",
                    "/api/payments/**", "/api/payments/v2-result",
                    "/api/products/**", "/api/products",
                    "/paymenttest", "/api/members/me",
                    "/auth/signin", "/auth/signup",
                    "/api/admin/**", "/api/admin/images", "/api/images/**",
                    "/product/**", "/shop/**",
                    "/api/admin/images/set-thumbnail", "/api/admin/images/set-thumbnail/**",
                    "/api/reviews", "/api/reviews/**", "/actuator/health"
                ).permitAll()
                .requestMatchers(HttpMethod.DELETE, "/admin/grade-rule/delete/**").authenticated()
                .requestMatchers(
                    "/api/members/me", "/api/mypage/**", "/api/orders/me",
                    "/api/message", "/api/admin/message/send",
                    "/admin/grade-rule/delete"
                ).authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 배포용 + 로컬 겸용 (쿠키 인증 사용을 전제로 credentials 허용)
        config.setAllowedOriginPatterns(List.of(
            // Prod
            "https://ibmall.shop",
            "https://www.ibmall.shop",
            "https://*.ibmall.shop",
            // (테스트용) CloudFront 기본 도메인을 잠깐 써야 하면 주석 해제
            // "https://d*.cloudfront.net",

            // Local/사내
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "http://192.168.*:*"
        ));

        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type","Authorization","X-Requested-With","Accept"));
        config.setAllowCredentials(true);   // fetch(..., { credentials: 'include' }) 필요
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
