package com.itbank.mall.config;

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
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// ✅ 비회원 접근 허용 경로
				.requestMatchers(
					"/",                          // 홈
					"/js/**",                     // JS 정적 리소스
					"/css/**",                    // CSS 정적 리소스
					"/images/**",                 // 이미지 정적 리소스
					"/api/auth/**",               // 로그인/로그아웃
					"/api/members/signup",        // 회원가입
					"/api/members/check-nickname",// 닉네임 중복확인
					"/api/email/**",              // 이메일 인증
					"/api/password/**",           // 비밀번호 재설정
					"/api/oauth2/**",             // 구글 OAuth
					"/api/payments/**",           // V1, V2 결제 모두
					"/api/payments/v2-result",   // 👈 이거 명시적으로 추가
					"/api/products/**",            // (선택) 상품 목록
					"/signin",
					"/signup",
					"/api/admin/**",
					"/api/admin/images",
					"/api/images/**",
					"/api/admin/product",
					"/product/**",
					"/shop/**",
					"/paymenttest"
				).permitAll()
			    // ✅ DELETE 요청 허용
			    .requestMatchers(HttpMethod.DELETE, "/admin/grade-rule/delete/**").authenticated()
				
				// ✅ 인증 필요한 경로
				.requestMatchers(
					"/api/members/me",
					"/api/mypage/**",
					"/api/orders/me",
					"/api/message",
					"/api/admin/message/send",			//이거 테스트용임 메세지보내는거 (관리자)
					"/api/admin/**",
					"/admin/grade-rule/delete"
				).authenticated()

				// ✅ 그 외 모든 요청 인증 필요
				.anyRequest().authenticated()
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
