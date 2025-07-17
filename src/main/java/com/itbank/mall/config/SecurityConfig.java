package com.itbank.mall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.itbank.mall.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// 비회원 허용
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/members/**", // ✅ 회원가입, 내 정보 조회, 수정 포함
					    "/api/members/**",      // 회원가입, 내 정보
					    "/api/email/**",        // 이메일 인증
					    "/api/password/**",     // 비밀번호 재설정
					    "/api/auth/**",         // 로그인/로그아웃
					    "/api/oauth2/**",       // 구글 인증 진입/콜백
					    "/api/oauth2/signup",    // 소셜 회원가입 (이거 꼭 필요!)
					    "/api/payments/v1/**",  // ✅ V1 결제 경로 허용
					    "/api/payments/v2/**"  // ✅ (선택) V2 결제도 허용할지 여부
						).permitAll()
			            .anyRequest().authenticated()
			        )
			        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  // ✅ 이 줄 추가
			        .build();
			}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
