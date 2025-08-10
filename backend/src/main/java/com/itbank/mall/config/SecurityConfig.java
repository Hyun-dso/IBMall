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
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // 💡 여기!
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
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://192.168.52.212:3000", "http://192.168.10.2:3000", "http://192.168.52.215:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);  // 캐시 시간 (초)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // 모든 경로에 적용
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
