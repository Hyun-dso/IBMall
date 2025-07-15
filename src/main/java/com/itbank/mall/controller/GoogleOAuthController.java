package com.itbank.mall.controller;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.itbank.mall.dto.GoogleSignupRequestDto;
import com.itbank.mall.dto.TempUserDto;
import com.itbank.mall.entity.Member;
import com.itbank.mall.service.MemberService;

import jakarta.servlet.http.HttpSession;
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
				.queryParam("client_id", clientId).queryParam("redirect_uri", redirectUri)
				.queryParam("response_type", "code").queryParam("scope", "openid profile email")
				.queryParam("access_type", "offline").build().toUriString();

		return ResponseEntity.ok(url);
	}

	@GetMapping("/signup-by-google")
	public String signupByGooglePage(HttpSession session, Model model) {
	    TempUserDto tempUser = (TempUserDto) session.getAttribute("tempGoogleUser");
	    if (tempUser == null) {
	        return "redirect:/";
	    }

	    model.addAttribute("email", tempUser.getEmail());
	    return "signup-by-google";  // → templates/signup-by-google.html 로 연결됨
	}
	
	@GetMapping("/callback/google")
	public ResponseEntity<?> googleCallback(@RequestParam("code") String code, HttpSession session) {
		log.info("📥 구글로부터 받은 code: {}", code);

		// 1. Access Token 요청을 위한 파라미터 구성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", code);
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("redirect_uri", redirectUri);
		params.add("grant_type", "authorization_code");

		// 2. WebClient로 access_token 요청
		WebClient webClient = WebClient.builder().baseUrl("https://oauth2.googleapis.com")
				.defaultHeader("Content-Type", "application/x-www-form-urlencoded").build();

		Map<String, Object> response = webClient.post().uri("/token").bodyValue(params).retrieve()
				.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
				}).block();

		log.info("🪙 토큰 응답: {}", response);

		String accessToken = (String) response.get("access_token");

		// 사용자 정보 요청
		Map<String, Object> userInfo = webClient.get().uri("https://www.googleapis.com/oauth2/v2/userinfo")
				.header("Authorization", "Bearer " + accessToken).retrieve()
				.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
				}).block();

		log.info("👤 구글 사용자 정보: {}", userInfo);
		
		// 3. 사용자 정보 추출
		String email = (String) userInfo.get("email");
		String name = (String) userInfo.get("name");
		String providerId = (String) userInfo.get("id");

		// 4. DB에 존재하는지 확인
		boolean exists = memberService.existsByEmail(email);
		if (!exists) {
		    // 세션에 임시 유저 정보 저장
		    session.setAttribute("tempGoogleUser", new TempUserDto(email, name, providerId));
		    // 클라이언트에 회원가입 페이지로 안내
		    return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
		                         .location(URI.create("/signup-by-google"))
		                         .build();
		}

		return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/signup-by-google"))
                .build();
	}
	
	@PostMapping("/signup-by-google")
	public ResponseEntity<?> signupByGoogle(@RequestBody GoogleSignupRequestDto dto,
	                                        HttpSession session) {
	    TempUserDto temp = (TempUserDto) session.getAttribute("tempGoogleUser");
	    if (temp == null) {
	        return ResponseEntity.badRequest().body("세션 만료: 다시 로그인하세요");
	    }

	    Member loginUser = memberService.signupByGoogle(dto, temp);
	    session.setAttribute("loginUser", loginUser);  // 로그인 처리
	    session.removeAttribute("tempGoogleUser");     // 임시 정보 제거

	    return ResponseEntity.ok("회원가입 및 로그인 완료!");
	}

}
