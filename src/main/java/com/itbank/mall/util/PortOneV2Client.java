package com.itbank.mall.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.itbank.mall.dto.payment.V2PaymentResultDto;

import lombok.RequiredArgsConstructor;

//📁 com.itbank.mall.util.PortOneV2Client.java

//com.itbank.mall.util.PortOneV2Client
@Component
@RequiredArgsConstructor
public class PortOneV2Client {

	private final WebClient webClient;

	@Value("${portone.api.secret}")
	private String secretKey;

	public V2PaymentResultDto getPaymentResultByTxId(String txId) {
		return webClient.get().uri("https://api.portone.io/payments/{txId}", txId)
				.header("Authorization", "PortOne " + secretKey).retrieve().bodyToMono(V2PaymentResultDto.class)
				.block(); // 서비스에서만 동기 블로킹 처리
	}
}
